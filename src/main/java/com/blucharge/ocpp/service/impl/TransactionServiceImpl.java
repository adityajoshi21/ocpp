package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
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

import javax.validation.Valid;
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



    @Override
    public StartTransactionResponse startTransaction(@Valid StartTransactionRequest request, String chargerIdentity) {

        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(request.getIdTag());
        if (!AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())){
            return new StartTransactionResponse().withIdTagInfo(idTagInfo);         //User isn't authorised
        }
        //also check for connector state / status


        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), request.getConnectorId());

        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(request.getIdTag());
        if(connectorRecord.getState().toUpperCase().equals("IDLE")) {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setIdTag(ocppTagRecord.getIdTag());
            transactionRecord.setConnectorId(connectorRecord.getId());
            transactionRecord.setConnectorName(connectorRecord.getName());
            transactionRecord.setChargerId(charger.getId().toString());
            transactionRecord.setMeterStartValue(request.getMeterStartValue());
            transactionRecord.setStartOn(request.getTimestamp());
            transactionRecord.setStatus(TransactionStatusUpdate.AfterStart.name());
            Long txnId = transactionsRepository.addTransaction(transactionRecord);

            log.info("Transaction accepted on Charger : {} with start value :{} and transaction Id : {}", chargerIdentity, request.getMeterStartValue(), txnId);

            //Update state for the connector on which txn started
            connectorRepository.updateConnectorState(txnId, connectorRecord.getId(), ConnectorState.CHARGING);
            //Update Connector  status
            connectorRepository.updateConnectorStatus(connectorRecord.getId(), request.getTimestamp(), ConnectorStatus.CHARGING);

        return  new StartTransactionResponse()
                .withIdTagInfo(idTagInfo)
                .withTransactionId(txnId);
        }
        log.error("Couldnt start transaction as Connector wasn't idle");
        return new StartTransactionResponse().withIdTagInfo(idTagInfo).withTransactionId(0l);
    }

    @Override
    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargerId) {

        Long transactionId = parameters.getTransactionId();
        String stopReason = parameters.getReason();
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerId);
        Long txnId = parameters.getTransactionId();
        //find connector id for transaction id from txn table
        Integer connectorNo = Math.toIntExact(transactionsRepository.findConnectorIdForTransactionId(txnId));
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(),connectorNo);
        if (connectorRecord.getState().toUpperCase().equals("CHARGING")) {
            TransactionRecord tr = transactionsRepository.getActiveTransctionForTxnId(transactionId);
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


                Long connectorPk = transactionsRepository.findConnectorIdForTransactionId(parameters.getTransactionId());
                connectorRepository.updateConnectorStatus(connectorPk, params.getStopTimestamp(), ConnectorStatus.AVAILABLE);
            }

            // Updating meter value for ongoing transaction
            if (parameters.isSetTransactionData()) {
                meterValueRepository.updateMeterValues(chargerId, parameters.getTransactionData(), transactionId);
            }

            // Get the authorization info of the user
            if (parameters.isSetIdTag()) {
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

        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), request.getConnectorId());
        Long connectorId = connectorRecord.getId();
        TransactionRecord tr = transactionsRepository.getActiveTransactionOnConnectorId(connectorId);
        if(!Objects.isNull(tr))
            log.info("There is an ongoing transaction in this connector id {}", connectorId);
        IdTagInfo info = ocppTagService.getIdTagInfo(request.getIdTag());
        RemoteStartTransactionResponse response = new RemoteStartTransactionResponse();
        if(AuthorizationStatus.ACCEPTED.name().equals(info.getStatus().toString())){
             response.setStatus(RemoteStartStopStatus.ACCEPTED);
             return  response;
        }
        response.setStatus(RemoteStartStopStatus.REJECTED);
        return response;
    }

    @Override
    public RemoteStopTransactionResponse remoteStopTransaction(RemoteStopTransactionRequest request, String chargerIdentity) {
        log.info("Remote Stop Transaction with params : {}", request);

        TransactionRecord tr  = transactionsRepository.getActiveTransctionForTxnId(request.getTransactionId());
        if(!Objects.isNull(tr)){
            transactionsRepository.stopChargingScreen(tr);
            RemoteStopTransactionResponse response = new RemoteStopTransactionResponse();
            response.setStatus(RemoteStartStopStatus.ACCEPTED);
            return response;
        }
        RemoteStopTransactionResponse invalidResponse = new RemoteStopTransactionResponse();
        invalidResponse.setStatus(RemoteStartStopStatus.REJECTED);
        return  invalidResponse;
    }
}