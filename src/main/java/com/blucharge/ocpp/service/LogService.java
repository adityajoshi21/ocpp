package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.dto.OcppLogResponseData;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;

import java.util.List;

public interface LogService {
    void processLogHisToryTempToHubWiseUpTime();

    void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto);

    List<OcppLogResponseData> generateLogForChargeBox(OcppLogRequestBody ocppLogRequestBody);
}
