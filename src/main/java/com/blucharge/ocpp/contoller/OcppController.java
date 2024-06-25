package com.blucharge.ocpp.contoller;

import com.blucharge.ocpp.dto.LogTempDataInsertRequestDto;
import com.blucharge.ocpp.dto.S3DataInsertRequestDto;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;
import com.blucharge.ocpp.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/api/v1/ocpp")
public class OcppController {
    @Autowired
    private LogService logService;
    @Autowired
    private ExecutorService executorService;

    @PostMapping(value = "/blucgn")
    public void handleIncomingMessage(@RequestBody OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto) {
        executorService.submit(() -> logService.handleIncomingMessage(ocppSocketDataFromBlucgnDto));
    }

    @GetMapping(value = "/process-temp-log")
    public void updateLogTempTable() {
        logService.processLogHisToryTempToHubWiseUpTime();
    }


    @PostMapping(value = "/insert-data-log-temp")
    public void addDataToTempLog(@RequestBody LogTempDataInsertRequestDto logTempDataInsertRequestDto) {
        logService.insertDataInTempTable(logTempDataInsertRequestDto);
    }

    @GetMapping(value = "/send-data-hub-analytics")
    public void sendDataToHubWiseAnalytics() {
        logService.sendDataToHubWiseAnalytics();
    }

    @PostMapping(value = "/send-log-data-s3")
    public void sendLogDataToS3(@RequestBody S3DataInsertRequestDto s3DataInsertRequestDto) {
        logService.sendDataToS3(s3DataInsertRequestDto);
    }

    @PostMapping(value = "/delete-log-data")
    public void deleteLogData(@RequestBody S3DataInsertRequestDto s3DataInsertRequestDto) {
        logService.deleteLogData(s3DataInsertRequestDto);
    }

}

