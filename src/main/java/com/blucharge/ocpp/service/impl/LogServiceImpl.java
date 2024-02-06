package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import com.blucharge.ocpp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogHistoryRepo logHistoryRepo;

    @Override
    public void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto) {
        LogHistoryRecord logHistoryRecord = new LogHistoryRecord();
        logHistoryRecord.setMessageId("RESPONSE");
        logHistoryRecord.setMessageName(ocppSocketDataFromBlucgnDto.getInputType());
        logHistoryRecord.setMessageJson(ocppSocketDataFromBlucgnDto.getInputData());
        logHistoryRecord.setMessageType(ocppSocketDataFromBlucgnDto.getInputType());
        logHistoryRecord.setChargerId(ocppSocketDataFromBlucgnDto.getChargeId());
        logHistoryRecord.setMessageOrigin("BLUCGN");
        logHistoryRepo.addRecord(logHistoryRecord);
    }
}
