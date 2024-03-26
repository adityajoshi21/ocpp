package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogRequestBody;

import java.util.List;

public interface LogHistoryRepo {
    void addRecord(LogHistoryRecord logHistoryRecord);

    List<LogHistoryRecord> getRecord(OcppLogRequestBody ocppLogRequestBody);
}
