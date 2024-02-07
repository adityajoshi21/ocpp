package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.MeterValueHistory;
import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;
import com.blucharge.db.ocpp.tables.records.MeterValueHistoryRecord;
import com.blucharge.ocpp.dto.SampledValue;
import com.blucharge.ocpp.repository.MeterValuesRepo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MeterValuesRepoImpl implements MeterValuesRepo {
    private static final MeterValueHistory meterValue = MeterValueHistory.METER_VALUE_HISTORY;
    @Autowired
    private DSLContext ctx;

    public MeterValuesRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void insertSampledValue(LiveTransactionRecord liveTransactionRecord, SampledValue sampledValue, DateTime timestamp) {
        MeterValueHistoryRecord meterValueHistoryRecord = new MeterValueHistoryRecord();
        meterValueHistoryRecord.setChargerId(liveTransactionRecord.getChargerId());
        meterValueHistoryRecord.setConnectorId(liveTransactionRecord.getConnectorId());
        meterValueHistoryRecord.setTransactionId(liveTransactionRecord.getChargingTransactionHistoryId());
        meterValueHistoryRecord.setSampledValueOn(timestamp);
        meterValueHistoryRecord.setValue(sampledValue.getValue());
        meterValueHistoryRecord.setReadingContext(sampledValue.getContext().name());
        meterValueHistoryRecord.setMeasurand(sampledValue.getMeasurand().name());
        MeterValueHistoryRecord meterValueHistoryRecord1 = ctx.newRecord(meterValue, meterValueHistoryRecord);
        meterValueHistoryRecord1.store();
    }
}
