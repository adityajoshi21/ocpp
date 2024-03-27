package com.blucharge.ocpp.contoller;

import com.blucharge.ocpp.dto.OcppLogRequestBody;
import com.blucharge.ocpp.dto.OcppLogResponseData;
import com.blucharge.ocpp.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/log")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping(value = "/download")
    public List<OcppLogResponseData> downloadLog(@RequestBody OcppLogRequestBody ocppLogRequestBody) {
        return logService.generateLogForChargeBox(ocppLogRequestBody);
    }
}
