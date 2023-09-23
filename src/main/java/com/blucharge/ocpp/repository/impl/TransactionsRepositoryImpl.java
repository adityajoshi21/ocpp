package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;
import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.repository.OcppTagRepository;
import com.blucharge.ocpp.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;


@Repository
@Slf4j
public class TransactionsRepositoryImpl implements TransactionsRepository {

    @Autowired
    private final DSLContext ctx;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private OcppTagRepository ocppTagRepository;

    public  TransactionsRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    private static final Transaction transaction = Transaction.TRANSACTION;


    @Override
    public void updateTransaction(UpdateTransactionParams params) {

        ctx.update(transaction)
                .set(transaction.STOP_ON, params.getStopTimestamp() == null ? DateTime.now() : params.getStopTimestamp())
                .set(transaction.METER_STOP_VALUE, params.getStopMeterValue())
                .set(transaction.STOP_REASON, params.getStopReason())
                .set(transaction.STATUS, TransactionStatusUpdate.AfterStop.name())
                .where(transaction.ID.eq(params.getTransactionId()))
                .execute();
    }

    @Override
    public TransactionRecord getActiveTransctionForTxnId(Long txnId) {
        return ctx.selectFrom(transaction)
                .where(transaction.ID.eq(txnId)
                        .and(transaction.STATUS.eq(TransactionStatusUpdate.AfterStart.name()))
                        .and(transaction.IS_ACTIVE.eq(true)))
                .fetchOneInto(TransactionRecord.class);
    }

    @Override
    public Long addTransaction(TransactionRecord rec) {
        TransactionRecord transactionRecord = ctx.newRecord(transaction, rec);
        transactionRecord.setIsActive(true);
        transactionRecord.store();
        return transactionRecord.getId();
    }

    @Override
    public Long findConnectorIdForTransactionId(Long transactionId) {

        return ctx.select(transaction.CONNECTOR_ID)
                .from(transaction)
                .where((transaction.ID.equal(transactionId)))
                .and(transaction.IS_ACTIVE.eq(true))
                .fetchOneInto(Long.class);
    }

    @Override
    public TransactionRecord getActiveTransactionOnConnectorId(Long connectorId) {
        return ctx.selectFrom(transaction)
                .where(transaction.CONNECTOR_ID.eq(connectorId))
                .and(transaction.STATUS.eq(TransactionStatusUpdate.AfterStart.name()))
                .and(transaction.IS_ACTIVE.eq(true))
                .fetchOneInto(TransactionRecord.class);
    }
    @Override
    public void stopChargingScreen(TransactionRecord transactionRecord){
        ctx.update(transaction)
                .set(transaction.STOP_REASON,"STOPPED")
                .set(transaction.STOP_ON,DateTime.now())
                .set(transaction.UPDATED_ON,DateTime.now())
                .where(transaction.IS_ACTIVE.eq(true))
                .and(transaction.ID.eq(transactionRecord.getId()))
                .execute();
    }
    public Boolean isTransactionRunningOnConnectorId(Long connectorPk) {
        int count = ctx.selectFrom(transaction)
                .where(transaction.CONNECTOR_ID.eq(connectorPk))
                .and(transaction.STATUS.eq(TransactionStatusUpdate.AfterStart.name()))
                .and(transaction.IS_ACTIVE.eq(true))
                .execute();
        if(Objects.isNull(count) || count > 1)
            return false;
        return count==1 ;
    }
}

//    @Override
//    public TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal) {
//        return ctx.select(transaction.fields()).from(transaction)
//                .join(connector, JoinType.JOIN).on(connector.ID.eq(transaction.CONNECTOR_ID))
//                .where(connector.CHARGER_ID.eq(chargerId))
//                .and(transaction.METER_START_VALUE.eq(meterStartVal))
//                .and(transaction.METER_STOP_VALUE.isNull())
//                .and(transaction.ID_TAG.eq(idTag))
//                .and(connector.IS_ACTIVE.equal(true))
//                .and(connector.CONNECTOR_NUMBER.eq(connectorId))
//                .fetchAnyInto(TransactionRecord.class);
//    }


