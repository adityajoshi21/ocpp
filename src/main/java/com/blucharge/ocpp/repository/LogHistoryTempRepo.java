package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import org.jooq.DSLContext;

public interface LogHistoryTempRepo {
    void createRecord(LogHistoryRecord logHistoryRecord, DSLContext dslOcppContext);
}
