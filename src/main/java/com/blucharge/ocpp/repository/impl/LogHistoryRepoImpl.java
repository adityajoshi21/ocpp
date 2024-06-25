package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.LogHistory;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogHistoryRepoImpl implements LogHistoryRepo {
    private static final LogHistory logHistory = LogHistory.LOG_HISTORY;

    @Override
    public void addRecord(LogHistoryRecord logHistoryRecord, DSLContext ctx) {
        LogHistoryRecord logHistoryRecord1 = ctx.newRecord(logHistory, logHistoryRecord);
        logHistoryRecord1.store();
    }

    @Override
    public List<LogHistoryRecord> getRecord(OcppLogRequestBody ocppLogRequestBody, DSLContext ctx) {
        return ctx.selectFrom(logHistory)
                .where(logHistory.CHARGER_ID.eq(ocppLogRequestBody.getChargeBoxId()))
                .and(logHistory.ID.greaterThan(ocppLogRequestBody.getOffset()))
                .fetchInto(LogHistoryRecord.class);
    }

    @Override
    public List<LogHistoryRecord> getLastOneDayRecord(DSLContext dslOcppContext) {
        return dslOcppContext.selectFrom(logHistory)
                .where(logHistory.CREATED_ON.greaterOrEqual(DateTime.now().minusDays(1)))
                .and(logHistory.CREATED_ON.lessOrEqual(DateTime.now()))
                .fetchInto(LogHistoryRecord.class);
    }

    @Override
    public List<LogHistoryRecord> getLastOneDayRecordWithTimeStamp(DSLContext dslOcppContext, DateTime time) {
        return dslOcppContext.selectFrom(logHistory)
                .where(logHistory.CREATED_ON.greaterOrEqual(time))
                .and(logHistory.CREATED_ON.lessOrEqual(time.minusDays(1)))
                .fetchInto(LogHistoryRecord.class);
    }

    @Override
    public List<LogHistoryRecord> getRecordForStartAndEnd(DateTime startTime, DateTime endTime, DSLContext ctx) {
        return ctx.selectFrom(logHistory)
                .where(logHistory.CREATED_ON.greaterOrEqual(startTime))
                .and(logHistory.CREATED_ON.lessOrEqual(endTime))
                .fetchInto(LogHistoryRecord.class);
    }

    @Override
    public void deleteRecord(Long id, DSLContext dslOcppContext) {
        dslOcppContext.deleteFrom(logHistory)
                .where(logHistory.ID.eq(id))
                .execute();
    }
}
