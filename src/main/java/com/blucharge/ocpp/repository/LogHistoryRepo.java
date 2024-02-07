package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;

public interface LogHistoryRepo {
    void addRecord(LogHistoryRecord logHistoryRecord);
}
