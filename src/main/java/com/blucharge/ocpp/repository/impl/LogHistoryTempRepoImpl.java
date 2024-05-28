package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.LogHistoryTemp;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.db.ocpp.tables.records.LogHistoryTempRecord;
import com.blucharge.ocpp.repository.LogHistoryTempRepo;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogHistoryTempRepoImpl implements LogHistoryTempRepo {
    private static final LogHistoryTemp logHistoryTemp = LogHistoryTemp.LOG_HISTORY_TEMP;

    @Override
    public void createRecord(LogHistoryRecord logHistoryRecord, DSLContext dslOcppContext) {
        LogHistoryTempRecord logHistoryTempRecord = dslOcppContext.newRecord(logHistoryTemp, logHistoryRecord);
        logHistoryTempRecord.store();
    }
}
