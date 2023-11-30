package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;
import com.blucharge.ocpp.dto.SampledValue;
import org.joda.time.DateTime;

public interface MeterValuesRepository {
    void insertSampledValue(LiveTransactionRecord liveTransactionRecord, SampledValue sampledValue, DateTime timestamp);
}
