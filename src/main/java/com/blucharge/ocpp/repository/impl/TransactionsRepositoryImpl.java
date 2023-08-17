package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Charger;
import com.blucharge.db.ocpp.tables.Connector;
import com.blucharge.db.ocpp.tables.OcppTag;
import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.InsertTransactionParams;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;
import com.blucharge.ocpp.enums.TransactionStatus;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.repository.OcppTagRepository;
import com.blucharge.ocpp.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

import static com.blucharge.ocpp.constants.OcppConstants.TEST_CHARGER;


@Repository
@Slf4j
public class TransactionsRepositoryImpl implements TransactionsRepository {

    @Autowired
    private final DSLContext ctx;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private OcppTagRepository ocppTagRepository;

    public TransactionsRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    private static final Transaction transaction = Transaction.TRANSACTION;
    private static final Connector connector = Connector.CONNECTOR;
    private static final Charger charger = Charger.CHARGER;
    private static final OcppTag ocppTag = OcppTag.OCPP_TAG;




    @Override
    public TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal) {
        return ctx.select(transaction.fields()).from(transaction)
                .join(connector, JoinType.JOIN).on(connector.ID.eq(transaction.CONNECTOR_ID))
                .where(connector.CHARGER_ID.eq(chargerId))
                .and(transaction.METER_START_VALUE.eq(meterStartVal))
                .and(transaction.METER_STOP_VALUE.isNull())
                .and(transaction.ID_TAG.eq(idTag))
                .and(connector.IS_ACTIVE.equal(true))
                .and(connector.CONNECTOR_NUMBER.eq(connectorId))
                .fetchAnyInto(TransactionRecord.class);
    }


    @Override
    public Long insertTransaction(InsertTransactionParams params, Long chargerId) {
        //Step 0 : Fetching connectorId from DB
        SelectConditionStep<Record1<Long>> connectorIdQuery = DSL.select(connector.ID)
                .from(connector).where(connector.CHARGER_ID.eq(chargerId)).and(connector.CONNECTOR_NUMBER.eq(params.getConnectorId()));

        //Step 1: Insert Transaction

        Long transactionId = ctx.insertInto(transaction)
                .set(transaction.CONNECTOR_ID, connectorIdQuery)      ///Setting connectorPk found by the query in Step 0
                .set(transaction.CHARGER_ID, TEST_CHARGER)
                .set(transaction.ID_TAG, params.getIdTag())
                .set(transaction.METER_START_VALUE, params.getStartMeterValue())
                .set(transaction.START_ON, params.getStartTimestamp())
                .set(transaction.METER_STOP_VALUE, params.getStartMeterValue())
                .set(transaction.STATUS, TransactionStatus.STARTED.name())
                .returning(transaction.ID)
                .fetchOne()
                .getId();

        log.info("Transaction created with Transaction ID : {}", transactionId);


        return transactionId;
    }

    @Override
    public Transaction updateTransaction(UpdateTransactionParams params) {
        // Step 1 : Update transaction table

        ctx.update(transaction)
                .set(transaction.STOP_ON, params.getStopTimestamp() == null ? DateTime.now() : params.getStopTimestamp())
                .set(transaction.METER_STOP_VALUE, params.getStopMeterValue())
                .set(transaction.STOP_REASON, params.getStopReason())
                .set(transaction.STATUS, TransactionStatus.STOPPED.name())
                .where(transaction.ID.eq(params.getTransactionId()))
                .execute();

            //If Transaction record exists in Transaction table and State is Charging change to IDLE
        return null;
    }

    @Override
    public TransactionRecord getActiveTransctionForTxnId(Long txnId) {
        Record result = ctx.select()
                .from(transaction)
                .join(connector).on(transaction.CONNECTOR_ID.eq(connector.ID))
                .join(charger).on(charger.CHARGER_ID.eq(charger.CHARGER_ID))
                .join(ocppTag).on(ocppTag.ID_TAG.eq(transaction.ID_TAG))
                .where(transaction.ID.eq(txnId))
                .and(transaction.IS_ACTIVE.eq(true))
                .fetchOne();

        if (result != null) {
            return map(result);
        }

        return null;
    }

    @Override
    public Long addTransaction(TransactionRecord record) {
        TransactionRecord record1 = ctx.newRecord(transaction, record);
        record1.store();
        return record1.getId();
    }

    private TransactionRecord map(Record r) {
        return TransactionRecord.builder()
                .id(r.getValue(transaction.ID))
                .chargeBoxId(r.getValue(connector.CHARGER_ID))
                .apiSource(r.getValue(charger.API_SOURCE))
                .connectorId(r.getValue(connector.CONNECTOR_NUMBER))
                .ocppIdTag(r.getValue(transaction.ID_TAG))
                .startTimestampDT(r.getValue(transaction.START_ON))
                .startTimestamp(DateTimeUtils.humanize(r.getValue(transaction.START_ON)))
                .startValue(r.getValue(transaction.METER_START_VALUE))
                .stopTimestampDT(r.getValue(transaction.STOP_ON))
                .stopTimestamp(DateTimeUtils.humanize(r.getValue(transaction.STOP_ON)))
                .stopValue(r.getValue(transaction.METER_STOP_VALUE))
                .stopReason(Optional.ofNullable(r.getValue(transaction.STOP_REASON)).orElse("COMPLETED"))
                .chargeBoxPk(r.getValue(charger.ID))
                .ocppTagPk(r.getValue(ocppTag.ID))
                .connectorPk(r.getValue(connector.ID))
                .createdTimestamp(r.getValue(transaction.CREATED_ON))
                .build();
    }




}
