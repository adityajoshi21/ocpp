package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.LiveTransaction;
import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;
import com.blucharge.ocpp.repository.LiveTransactionRepo;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LiveTransactionRepoImpl implements LiveTransactionRepo {
    private static final LiveTransaction liveTransaction = LiveTransaction.LIVE_TRANSACTION;
    @Autowired
    private final DSLContext ctx;

    public LiveTransactionRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void createRecord(LiveTransactionRecord liveTransactionRecord) {
        LiveTransactionRecord liveTransactionRecord1 = ctx.newRecord(liveTransaction, liveTransactionRecord);
        liveTransactionRecord1.store();
    }

    @Override
    public void updateTransactionForMeterValue(Long transactionId, String socMeasurand, String energyImported) {
        ctx.update(liveTransaction)
                .set(liveTransaction.CURRENT_SOC, Integer.valueOf(socMeasurand))
                .set(liveTransaction.UNIT_CONSUMED, Double.valueOf(energyImported))
                .set(liveTransaction.UPDATED_ON, DateTime.now())
                .where(liveTransaction.CHARGING_TRANSACTION_HISTORY_ID.eq(transactionId))
                .and(liveTransaction.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public LiveTransactionRecord getLiveTransactionRecordForTxnId(long txnId) {
        return ctx.selectFrom(liveTransaction)
                .where(liveTransaction.IS_ACTIVE.eq(true))
                .and(liveTransaction.CHARGING_TRANSACTION_HISTORY_ID.eq(txnId))
                .fetchOneInto(LiveTransactionRecord.class);
    }

    @Override
    public void deleteRecord(Long liveTxnId) {
        ctx.deleteFrom(liveTransaction)
                .where(liveTransaction.ID.eq(liveTxnId))
                .and(liveTransaction.IS_ACTIVE.eq(true))
                .execute();
    }
}
