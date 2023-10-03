package com.blucharge.ocpp.repository;

import com.blucharge.ocpp.dto.ws.MeterValue;
import org.jooq.DSLContext;

import java.util.List;

public interface MeterValueRepository {
    void insertMeterValues(Long chargerId, List<MeterValue> meterValues, Integer connectorId, Long transactionId);
    void updateMeterValues(Long chargerId, List<MeterValue> list, Long transactionId);
    void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, Long chargerId, Integer connectorNo, Long transactionId);
}
