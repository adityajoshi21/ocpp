package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.LogHistory;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogHistoryRepoImpl implements LogHistoryRepo {
    private static final LogHistory logHistory = LogHistory.LOG_HISTORY;
    @Autowired
    private final DSLContext ctx;

    public LogHistoryRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void addRecord(LogHistoryRecord logHistoryRecord) {
        LogHistoryRecord logHistoryRecord1 = ctx.newRecord(logHistory, logHistoryRecord);
        logHistoryRecord1.store();
    }

    @Override
    public List<LogHistoryRecord> getRecord(OcppLogRequestBody ocppLogRequestBody) {
        return ctx.selectFrom(logHistory)
                .where(logHistory.CHARGER_ID.eq(ocppLogRequestBody.getChargeBoxId()))
                .and(logHistory.ID.greaterThan(ocppLogRequestBody.getOffset()))
                .fetchInto(LogHistoryRecord.class);
    }
}
