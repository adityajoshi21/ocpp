package com.blucharge.ocpp.repository;

import com.blucharge.ocpp.dto.ws.MeterValue;
import org.jooq.DSLContext;

import java.util.List;

public interface MeterValueRepository {
    void insertMeterValues(String chargerIdentity, List<MeterValue> meterValues, Long connectorId, Long transactionId);
    void updateMeterValues(String chargeBoxIdentity, List<MeterValue> list, Long transactionId);
    void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, Long connectorPk, Long transactionId);
}
