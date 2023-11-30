package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.ChargingTransactionHistory;
import com.blucharge.db.ocpp.tables.records.ChargingTransactionHistoryRecord;
import com.blucharge.ocpp.enums.Reason;
import com.blucharge.ocpp.repository.ChargingTransactionHistoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
@Slf4j
public class ChargingTransactionHistoryRepoImpl implements ChargingTransactionHistoryRepo {

    private static final ChargingTransactionHistory chargingTransactionHistory = ChargingTransactionHistory.CHARGING_TRANSACTION_HISTORY;
    @Autowired
    private final DSLContext ctx;


    public ChargingTransactionHistoryRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public ChargingTransactionHistoryRecord createRecord(ChargingTransactionHistoryRecord chargingTransactionHistoryRecord) {
        ChargingTransactionHistoryRecord chargingTransactionHistoryRecord1 = ctx.newRecord(chargingTransactionHistory, chargingTransactionHistoryRecord);
        chargingTransactionHistoryRecord1.store();
        return chargingTransactionHistoryRecord1;
    }

    @Override
    public void updateTransactionForStopTransaction(Integer transactionId, Integer meterStopValue, DateTime stopTime, Reason reason, Long startTime, Integer meterStartValue) {
        ctx.update(chargingTransactionHistory)
                .set(chargingTransactionHistory.METER_STOP_VALUE, meterStopValue)
                .set(chargingTransactionHistory.STOP_TIME, stopTime.getMillis())
                .set(chargingTransactionHistory.STOP_REASON, reason.name())
                .set(chargingTransactionHistory.RUNNING_TIME, stopTime.getMillis() - startTime)
                .set(chargingTransactionHistory.UNIT_CONSUMED, (double) (meterStopValue - meterStartValue / 1000) + (0.0 + Double.valueOf("0." + ((meterStopValue - meterStartValue) % 1000))))
                .where(chargingTransactionHistory.ID.eq(transactionId.longValue()))
                .and(chargingTransactionHistory.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public ChargingTransactionHistoryRecord getChargingTxnHistoryRecordForUuid(String txnId) {
        return ctx.selectFrom(chargingTransactionHistory)
                .where(chargingTransactionHistory.IS_ACTIVE.eq(true))
                .and(chargingTransactionHistory.UUID.eq(txnId))
                .fetchOneInto(ChargingTransactionHistoryRecord.class);
    }
}



