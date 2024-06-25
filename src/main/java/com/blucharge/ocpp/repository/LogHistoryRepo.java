package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import org.joda.time.DateTime;
import org.jooq.DSLContext;

import java.util.List;

public interface LogHistoryRepo {
    void addRecord(LogHistoryRecord logHistoryRecord, DSLContext dslContext);

    List<LogHistoryRecord> getRecord(OcppLogRequestBody ocppLogRequestBody, DSLContext ctx);

    List<LogHistoryRecord> getLastOneDayRecord(DSLContext dslOcppContext);

    List<LogHistoryRecord> getLastOneDayRecordWithTimeStamp(DSLContext dslOcppContext, DateTime time);

    List<LogHistoryRecord> getRecordForStartAndEnd(DateTime startTime, DateTime endTime, DSLContext ctx);

    void deleteRecord(Long id, DSLContext dslOcppContext);
}
