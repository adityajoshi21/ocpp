package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.*;
import com.blucharge.ocpp.dto.api.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.enums.*;
import com.blucharge.ocpp.repository.*;
import com.blucharge.ocpp.service.OcppTagService;
import com.blucharge.ocpp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private OcppTagService ocppTagService;
    @Autowired
    private OcppTagRepository ocppTagRepository;
    @Autowired
    private ChargerRepository chargerRepository;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private MeterValueRepository meterValueRepository;
    @Autowired
    private TransactionSummaryRepository transactionSummaryRepository;

    @Override
    public StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerIdentity) {

        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(request.getIdTag());
        if (!AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())){
            return new StartTransactionResponse().withIdTagInfo(idTagInfo);         //User isn't authorised
        }


        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), request.getConnectorId());  //fetch connector at which txn has started
        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(request.getIdTag());      //get user who started txn
            //check if connector exists in DB

        if(!Objects.isNull(connectorRecord) && (connectorRecord.getState().toUpperCase().equals("IDLE") || connectorRecord.getState().toUpperCase().equals("WAITING_FOR_CHARGER_RESPONSE"))) {       //check for connector state  before changing its state
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setIdTag(ocppTagRecord.getIdTag());
            transactionRecord.setConnectorNumber(request.getConnectorId());
            transactionRecord.setChargerId(charger.getId());
            transactionRecord.setMeterStartValue(request.getMeterStartValue());
            transactionRecord.setStartOn(request.getTimestamp());
            transactionRecord.setState(TransactionStateUpdate.AfterStart.toString());
            Long txnId = transactionsRepository.addTransaction(transactionRecord);

            log.info("Transaction accepted on Charger : {} with start value : {} and transaction Id : {}", chargerIdentity, request.getMeterStartValue(), txnId);

            //Update state for the connector on which txn started
            connectorRepository.updateConnectorState(txnId, connectorRecord.getId(), ConnectorState.CHARGING);
            //Update Connector  status
            connectorRepository.updateConnectorStatus(connectorRecord.getId(), request.getTimestamp(), ConnectorStatus.CHARGING);

            TransactionSummaryRecord transactionSummaryRecord = transactionSummaryRepository.doesTransactionExistsInTransactionHistory(txnId);
            if(Objects.isNull(transactionSummaryRecord)) {
                transactionSummaryRepository.addTransactionInTransactionSummary(txnId, request.getIdTag(),charger.getId(), request.getConnectorId());
            }
            return  new StartTransactionResponse()
                .withIdTagInfo(idTagInfo)
                .withTransactionId(txnId);
        }
        log.error("Couldn't start transaction as there is an ongoing transaction on the connector");
        return new StartTransactionResponse().withIdTagInfo(idTagInfo).withTransactionId(-1l);
    }

    @Override
    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargerId) {

        Long transactionId = parameters.getTransactionId();
        String stopReason = parameters.getReason();
        Boolean update = false;
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerId).get(0);
        Long txnId = parameters.getTransactionId();
        Integer connectorNo = transactionsRepository.findConnectorNoForTransactionId(txnId);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(),connectorNo);
        if (!Objects.isNull(connectorRecord) && (connectorRecord.getState().toUpperCase().equals("CHARGING") || connectorRecord.getStatus().toUpperCase().equals("FINISHING"))) {
            TransactionRecord tr = transactionsRepository.getActiveTransactionOnConnectorNoForTxnId(transactionId, connectorNo);
            if (!Objects.isNull(tr)) {
                log.info("Transaction found : {}", tr);
                if (new BigDecimal(String.valueOf(tr.getMeterStartValue())).setScale(0, RoundingMode.HALF_EVEN).intValueExact() > parameters.getMeterStopValue().setScale(0, RoundingMode.HALF_EVEN).intValueExact()) {
                    parameters.setMeterStopValue(BigDecimal.valueOf(tr.getMeterStartValue().setScale(0, RoundingMode.HALF_EVEN).intValueExact()));
                }
                UpdateTransactionParams params =
                        UpdateTransactionParams.builder()
                                .chargerId(charger.getId())
                                .transactionId(transactionId)
                                .stopTimestamp(parameters.getTimestamp())
                                .stopMeterValue((parameters.getMeterStopValue()))
                                .stopReason(stopReason)
                                .transactionData(parameters.getTransactionData())
                                .build();

                log.info("Stop transaction with parameters : {}", params);


                //update in Transaction table
                transactionsRepository.updateTransaction(params);

                //Set Connector State back to IDLE again
                connectorRepository.updateConnectorState(transactionId, connectorRecord.getId(), ConnectorState.IDLE);


                Integer connectorNumber = transactionsRepository.findConnectorNoForTransactionId(parameters.getTransactionId());
                ConnectorRecord record = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), connectorNumber);
                connectorRepository.updateConnectorStatus(record.getId(), params.getStopTimestamp(), ConnectorStatus.AVAILABLE);
                update = true;
            }

            // Updating meter value for ongoing transaction
            if (parameters.isSetTransactionData()) {
                meterValueRepository.updateMeterValues(charger.getId(), parameters.getTransactionData(), transactionId);
            }

            // Updating transaction in transaction history table
            if(update){
                TransactionRecord transactionRecord = transactionsRepository.getInactiveTransactionOnConnectorId(transactionId, connectorNo);
                Long startOn = transactionRecord.getStartOn().getMillis();
                Long stopOn = transactionRecord.getStopOn().getMillis();
                if(!Objects.isNull(transactionRecord.getEndSoc()) && !Objects.isNull(transactionRecord.getStartSoc())){
                BigDecimal socGain = transactionRecord.getEndSoc().subtract(transactionRecord.getStartSoc());
                BigDecimal unitsConsumed = transactionRecord.getMeterStopValue().subtract(transactionRecord.getMeterStartValue());
                Long duration = stopOn - startOn;
                transactionSummaryRepository.updateTransactionInTransactionSummary(parameters.getTransactionId(), unitsConsumed, duration, socGain, transactionRecord.getStopReason());
                }
                else
                    log.info("Meter values not set!");
            }
            // Get the authorization info of the user
            if(parameters.isSetIdTag()) {
                IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(parameters.getIdTag());
                return new StopTransactionResponse().withIdTagInfo(idTagInfo);
            } else {
                return new StopTransactionResponse();
            }
        }
        log.error("Active transaction not found, i.e to be stopped");
        return null;
    }

    @Override
    public RemoteStartTransactionResponse remoteStartTransaction(RemoteStartTransactionRequest request, String chargerIdentity) {

        log.info("Remote Start Transaction with params : {}", request);
        if (!Objects.isNull(request.getConnectorId())) {
            Integer connectorNo = request.getConnectorId();
            ChargerRecord chargerRecord = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);


            ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(chargerRecord.getId(), connectorNo);

            if (!Objects.isNull(connectorRecord)) {
                IdTagInfo info = ocppTagService.getIdTagInfo(request.getIdTag());

                if (!AuthorizationStatus.ACCEPTED.name().equals((info.getStatus()).toString())) {
                    RemoteStartTransactionResponse response = new RemoteStartTransactionResponse();
                    response.setStatus(RemoteStartStopStatus.REJECTED);
                    return response;
                }

                TransactionRecord tr = transactionsRepository.getActiveTransactionOnConnectorId(request.getConnectorId(), chargerRecord.getId());
                if (!Objects.isNull(tr)) {
                    log.info("There is an ongoing transaction in this connector id {}", request.getConnectorId());
                    RemoteStartTransactionResponse response = new RemoteStartTransactionResponse();
                    response.setStatus(RemoteStartStopStatus.REJECTED);
                    return response;
                } else {
                    RemoteStartTransactionResponse response = new RemoteStartTransactionResponse();
                    response.setStatus(RemoteStartStopStatus.ACCEPTED);
                    log.info("Remote Txn started on Charger Id : {}, on Connector No {}", connectorRecord.getChargerId(), connectorNo);
                    //ToDo: move to repository

                    connectorRecord.setStatus(ConnectorStatus.PREPARING.name());
                    connectorRecord.setState(ConnectorState.WAITING_FOR_CHARGER_RESPONSE.name());
                    connectorRecord.update();
                    return response;
                }
            }
            log.info("Connector not found on which txn is to be started");
            return null;
        }
        log.info("Connector Id wasn't sent in request");

        ChargerRecord chargerRecord = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);
        Integer connectorNo = chargerRepository.findNoOfConnectorsForCharger(chargerRecord.getId());


        for (int i = 1; i <= connectorNo; i++) {
            ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(chargerRecord.getId(), i);
            IdTagInfo info = ocppTagService.getIdTagInfo(request.getIdTag());
            if (connectorRecord.getState().equalsIgnoreCase("Idle") && AuthorizationStatus.ACCEPTED.name().equals((info.getStatus()).toString())) {
                RemoteStartTransactionResponse response = new RemoteStartTransactionResponse();
                response.setStatus(RemoteStartStopStatus.ACCEPTED);
                connectorRecord.setStatus(ConnectorStatus.PREPARING.name());
                connectorRecord.setState(ConnectorState.WAITING_FOR_CHARGER_RESPONSE.name());
                connectorRecord.update();
                log.info("Remote Txn started on Charger Id : {}, on Connector No {}", connectorRecord.getChargerId(), i);
                return response;
            }
        }
          RemoteStartTransactionResponse remoteStartTransactionResponse = new RemoteStartTransactionResponse();
          remoteStartTransactionResponse.setStatus(RemoteStartStopStatus.REJECTED);
          return remoteStartTransactionResponse;

    }

    @Override
    public RemoteStopTransactionResponse remoteStopTransaction(RemoteStopTransactionRequest request, String chargerIdentity) {

        log.info("Remote Stop Transaction with params : {}", request);
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);
        Integer connectorNo = transactionsRepository.findConnectorNoForTransactionId(request.getTransactionId());
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), connectorNo);
        TransactionRecord tr  = transactionsRepository.getActiveTransactionOnConnectorNoForTxnId(request.getTransactionId(), connectorNo);

        if(!Objects.isNull(tr)){
//            transactionsRepository.stopChargingInitiatedFromRemoteStart(tr);
            RemoteStopTransactionResponse response = new RemoteStopTransactionResponse();
            response.setStatus(RemoteStartStopStatus.ACCEPTED);
//ToDo: move to repository
            connectorRecord.setStatus(ConnectorStatus.FINISHING.name());
            connectorRecord.setState(ConnectorState.STOPPING.name());
            connectorRecord.update();
            return response;
        }
        RemoteStopTransactionResponse invalidResponse = new RemoteStopTransactionResponse();
        invalidResponse.setStatus(RemoteStartStopStatus.REJECTED);
        return  invalidResponse;
    }
}