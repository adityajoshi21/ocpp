package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.enums.AuthorizationStatus;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.TransactionStatus;
import com.blucharge.ocpp.repository.*;
import com.blucharge.ocpp.service.OcppTagService;
import com.blucharge.ocpp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


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
    public StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerIdentity) {


        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(request.getIdTag());
        if (!AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())){
            return new StartTransactionResponse().withIdTagInfo(idTagInfo); //User isn't authorised
        }


        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), request.getConnectorId());

        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(request.getIdTag());

        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setIdTag(ocppTagRecord.getIdTag());
        transactionRecord.setConnectorId(connectorRecord.getId());
        transactionRecord.setConnectorName(connectorRecord.getName());
        transactionRecord.setMeterStartValue(request.getMeterStartValue());
        transactionRecord.setStartOn(request.getTimestamp());
        transactionRecord.setStatus(TransactionStatus.STARTED.name());
        Long txnId = transactionsRepository.addTransaction(transactionRecord);

        log.info("Transaction accepted on Charger : {} with start value :{} and transaction Id : {}", chargerIdentity, request.getMeterStartValue(), txnId);

        //Update state for the connector on which txn started
        connectorRepository.updateConnectorState(txnId, connectorRecord.getId(), ConnectorState.CHARGING);

        return  new StartTransactionResponse()
                .withIdTagInfo(idTagInfo)
                .withTransactionId(txnId);
    }

    @Override
    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargerId) {

        Long transactionId = parameters.getTransactionId();
        String stopReason = parameters.getReason();
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerId);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorFromConnectorNameAndChargerId(parameters.getConnectorName(),charger.getId());

        TransactionRecord tr = transactionsRepository.getActiveTransctionForTxnId(transactionId);
        log.info("Transaction found : {}", tr);
        if(tr!=null) {

            if (new BigDecimal(String.valueOf(tr.getMeterStartValue())).setScale(0, RoundingMode.HALF_EVEN).intValueExact() > parameters.getMeterStopValue().setScale(0, RoundingMode.HALF_EVEN).intValueExact())
            {
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

            //TODO move to repo and do not remove
            Long connectorPkQuery = transactionsRepository.findConnectorPkForTransactionId(parameters.getTransactionId());
            connectorRepository.updateConnectorStatus(connectorPkQuery, params.getStopTimestamp(), params.getStatusUpdate());
        }

        // Updating meter value for ongoing transaction
        if (parameters.isSetTransactionData()) {
            meterValueRepository.updateMeterValues(chargerId, parameters.getTransactionData(), transactionId);
        }

        // Get the authorization info of the user
        if (parameters.isSetIdTag( )) {
            IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(parameters.getIdTag());
            return new StopTransactionResponse().withIdTagInfo(idTagInfo);
        } else {
            return new StopTransactionResponse();
        }
    }

    @Override
    public RemoteStartTransactionResponse remoteStart(RemoteStartTransactionRequest request, String chargerIdentity) {
        return null;
    }

    @Override
    public RemoteStopTransactionResponse remoteStop(RemoteStopTransactionRequest request, String chargerIdentity) {
        return null;
    }
}