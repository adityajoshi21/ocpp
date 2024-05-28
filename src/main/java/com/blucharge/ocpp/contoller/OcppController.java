package com.blucharge.ocpp.contoller;

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
        executorService.submit(() -> {
            logService.handleIncomingMessage(ocppSocketDataFromBlucgnDto);
        });
    }

    @GetMapping(value = "/update-analytics")
    public void updateAnalytics() {
        logService.processLogHisToryTempToHubWiseUpTime();
    }
}

