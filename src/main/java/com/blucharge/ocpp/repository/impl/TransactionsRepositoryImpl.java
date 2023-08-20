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




    @Override
    public void updateTransaction(UpdateTransactionParams params) {

        ctx.update(transaction)
                .set(transaction.STOP_ON, params.getStopTimestamp() == null ? DateTime.now() : params.getStopTimestamp())
                .set(transaction.METER_STOP_VALUE, params.getStopMeterValue())
                .set(transaction.STOP_REASON, params.getStopReason())
                .set(transaction.STATUS, TransactionStatus.STOPPED.name())
                .where(transaction.ID.eq(params.getTransactionId()))
                .execute();

    }

    @Override
    public TransactionRecord getActiveTransctionForTxnId(Long txnId) {
        TransactionRecord rec = ctx.selectFrom(transaction)
                .where(transaction.ID.eq(txnId)
                        .and(transaction.IS_ACTIVE.eq(true))).fetchOneInto(TransactionRecord.class);
        return  rec;
    }

    @Override
    public Long addTransaction(TransactionRecord record) {
        TransactionRecord transactionRecord = ctx.newRecord(transaction, record);
        transactionRecord.setIsActive(true);
        transactionRecord.store();
        return transactionRecord.getId();
    }

    @Override
    public Long findConnectorPkForTransactionId(Long transactionId) {
        Long connectorPkQuery = ctx.select(transaction.CONNECTOR_ID)
                .from(transaction)
                .where((transaction.ID.equal(transactionId)))
                .fetchOneInto(Long.class);
        return connectorPkQuery;
    }

}
