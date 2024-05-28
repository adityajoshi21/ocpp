package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;

import java.util.List;

public interface LogHistoryRepo {
    void addRecord(LogHistoryRecord logHistoryRecord, DSLContext dslContext);

    List<LogHistoryRecord> getRecord(OcppLogRequestBody ocppLogRequestBody, DSLContext ctx);

    List<LogHistoryRecord> getLastOneDayRecord(DefaultDSLContext dslOcppContext);
}
