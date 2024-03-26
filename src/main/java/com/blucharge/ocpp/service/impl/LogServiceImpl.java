package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.OcppLogData;
import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.dto.OcppLogResponseData;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import com.blucharge.ocpp.service.LogService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogHistoryRepo logHistoryRepo;

    @Override
    public void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto) {
        LogHistoryRecord logHistoryRecord = new LogHistoryRecord();
        logHistoryRecord.setMessageType("RESPONSE");
        logHistoryRecord.setMessageName(ocppSocketDataFromBlucgnDto.getInputType());
        logHistoryRecord.setMessageJson(ocppSocketDataFromBlucgnDto.getInputData());
        logHistoryRecord.setChargerId(ocppSocketDataFromBlucgnDto.getChargeId());
        logHistoryRecord.setMessageOrigin("BLUCGN");
        logHistoryRepo.addRecord(logHistoryRecord);
    }

    @Override
    public List<OcppLogResponseData> generateLogForChargeBox(OcppLogRequestBody ocppLogRequestBody) {
        List<OcppLogResponseData> response = new ArrayList<>();
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getRecord(ocppLogRequestBody);
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            if (logHistoryRecord.getCreatedOn().getMillis() >= ocppLogRequestBody.getStartTime() && logHistoryRecord.getCreatedOn().getMillis() <= ocppLogRequestBody.getEndTime()) {
                OcppLogResponseData ocppLogResponseData = new OcppLogResponseData();
                ocppLogResponseData.setId(logHistoryRecord.getId());
                ocppLogResponseData.setChargeBoxId(logHistoryRecord.getChargerId());
                ocppLogResponseData.setMessageName(logHistoryRecord.getMessageName());
                ocppLogResponseData.setCreatedOn(logHistoryRecord.getCreatedOn().getMillis());
                OcppLogData ocppLogData = new Gson().fromJson(logHistoryRecord.getMessageJson(), OcppLogData.class);
                ocppLogResponseData.setDataJson(new Gson().toJson(ocppLogData));
                response.add(ocppLogResponseData);
            }
        }
        return response;
    }
}
