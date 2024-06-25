package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.LogTempDataInsertRequestDto;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.dto.OcppLogResponseData;
import com.blucharge.ocpp.dto.S3DataInsertRequestDto;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;

import java.util.List;

public interface LogService {
    void processLogHisToryTempToHubWiseUpTime();

    void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto);

    List<OcppLogResponseData> generateLogForChargeBox(OcppLogRequestBody ocppLogRequestBody);

    void insertDataInTempTable(LogTempDataInsertRequestDto logTempDataInsertRequestDto);

    void sendDataToHubWiseAnalytics();

    void sendDataToS3(S3DataInsertRequestDto s3DataInsertRequestDto);

    void deleteLogData(S3DataInsertRequestDto s3DataInsertRequestDto);
}
