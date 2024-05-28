package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.db.ocpp.tables.records.LogHistoryTempRecord;
import org.jooq.DSLContext;

import java.util.List;

public interface LogHistoryTempRepo {
    void createRecord(LogHistoryRecord logHistoryRecord, DSLContext dslOcppContext);
}
