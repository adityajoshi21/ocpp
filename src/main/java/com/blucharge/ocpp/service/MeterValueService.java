package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.ws.MeterValueRequest;
import com.blucharge.ocpp.dto.ws.MeterValueResponse;

public interface MeterValueService {
    MeterValueResponse meterValue(MeterValueRequest request, String chargerId);
}
