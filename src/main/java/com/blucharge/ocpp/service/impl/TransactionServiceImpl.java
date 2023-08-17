package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.Connector;
import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.enums.AuthorizationStatus;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import com.blucharge.ocpp.repository.*;
import com.blucharge.ocpp.service.OcppTagService;
import com.blucharge.ocpp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.blucharge.ocpp.constants.OcppConstants.TEST_CHARGER;

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
    private static final Connector connector = Connector.CONNECTOR;


    @Override
    public StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerIdentity) {


        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(request.getIdTag(), TEST_CHARGER, request.getTimestamp());
        if (!AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())){
            return new StartTransactionResponse().withIdTagInfo(idTagInfo);
        }


        ChargerRecord charger = chargerRepository.getChargerFromChargerId(TEST_CHARGER);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(), request.getConnectorId());

        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(request.getIdTag());

//        TransactionRecord transaction = transactionsRepository.getTransactionForParams(charger.getId(), request.getIdTag(), request.getConnectorId(), request.getMeterStartValue());
//        Long txnId = null;
//        if(transaction == null || "-1".equals(transaction.getMeterStartValue())) {
            TransactionRecord record = new TransactionRecord();
            record.setIdTag(ocppTagRecord.getIdTag());
            record.setConnectorId(connectorRecord.getId());
            record.setConnectorName(connectorRecord.getName());
            record.setMeterStartValue(request.getMeterStartValue());
            record.setStartOn(request.getTimestamp());
            Long txnId = transactionsRepository.addTransaction(record);


            //Update OCPP_TAG table, set IN_TRANSACTION = true for the user who started txn
            ocppTagRepository.updateInTransactionForIdTag(request.getIdTag(), Boolean.TRUE);
            log.info("Transaction accepted on Charger : {} with start value :{} and transaction Id : {}", TEST_CHARGER, request.getMeterStartValue(), txnId);

            //Update status for the connector on which txn started
            SelectConditionStep<Record1<Long>> connectorPkQuery =
                    DSL.select(connector.ID)
                            .from(connector)
                            .where(connector.CHARGER_ID.equal(charger.getId()))
                            .and(connector.CONNECTOR_NUMBER.equal(request.getConnectorId()));


            connectorRepository.insertConnectorStatus(connectorPkQuery,  request.getTimestamp(), TransactionStatusUpdate.AfterStart);
            connectorRepository.updateConnectorStateFromIdle(txnId, connectorRecord.getId(), ConnectorState.CHARGING);
//        }
//         else
//             txnId = transaction.getId();


        return  new StartTransactionResponse()
                .withIdTagInfo(idTagInfo)
                .withTransactionId(txnId);
    }

    @Override
    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargerId) {
        Long transactionId = parameters.getTransactionId();
        String stopReason = parameters.getReason();


        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerId);

        TransactionRecord txnrecord = transactionsRepository.getActiveTransctionForTxnId(transactionId);
        log.info("Transaction found : {}", txnrecord);
        if(txnrecord!=null)
            parameters.setMeterStop(BigDecimal.valueOf(new BigDecimal(String.valueOf(txnrecord.getMeterStartValue())).setScale(0, RoundingMode.HALF_EVEN).intValueExact()));
        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargerId(charger.getId())
                        .transactionId(transactionId)
                        .stopTimestamp(parameters.getTimestamp())
                        .stopMeterValue((parameters.getMeterStop()))
                        .stopReason(stopReason)
                        .build();

        log.info("Stop transaction with parameters : {}", params);
        //updateTxn
        Transaction transaction = transactionsRepository.updateTransaction(params);

        // Step 3 : Set Connector Status back to AVAILABLE again

        SelectConditionStep<Record1<Long>> connectorPkQuery = DSL.select(transaction.CONNECTOR_ID)
                .from(transaction)
                .where((transaction.ID.equal(params.getTransactionId())));
        connectorRepository.insertConnectorStatus (connectorPkQuery, params.getStopTimestamp(), params.getStatusUpdate());



        ocppTagRepository.updateInTransactionForIdTag(parameters.getIdTag(), Boolean.FALSE);
        if (parameters.isSetTransactionData()) {
            meterValueRepository.insertMeterValues(chargerId, parameters.getTransactionData(), transactionId);  //To Do
        }

        // Get the authorization info of the user
        if (parameters.isSetIdTag( )) {
            IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(parameters.getIdTag());
            return new StopTransactionResponse().withIdTagInfo(idTagInfo);
        } else {
            return new StopTransactionResponse();
        }
    }
}
