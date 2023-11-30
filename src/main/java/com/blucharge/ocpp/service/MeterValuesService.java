package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.meter_value.MeterValuesRequest;
import com.blucharge.ocpp.dto.meter_value.MeterValuesResponse;

public interface MeterValuesService {
    MeterValuesResponse insertMeterValue(MeterValuesRequest request, String chargerId);
}
