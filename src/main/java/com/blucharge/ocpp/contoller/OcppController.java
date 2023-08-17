package com.blucharge.ocpp.contoller;

import com.blucharge.core.dto.ResponseDto;
import com.blucharge.core.enums.ErrorCodes;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.enums.OcppProtocol;
import com.blucharge.ocpp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.blucharge.ocpp.constants.OcppConstants.TEST_CHARGER;

@Slf4j
@RestController
@RequestMapping("api/v1/ocpp")

public class OcppController {
    @Autowired
    private ChargerService chargerService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OcppTagService ocppTagService;
    @Autowired
    private MeterValueService meterValueService;

    @PostMapping(value = "/boot-notification")
    public ResponseDto<BootNotificationResponse> handleBootNotification(@RequestBody BootNotificationRequest request) {
        try {
            return new ResponseDto<>(
                    "SUCCESS_MESSAGE",
                    ErrorCodes.NO_ERROR.getErrorCode(),
                    ErrorCodes.NO_ERROR.getErrorMsg(),
                    chargerService.bootNotification(request, OcppProtocol.V_16_JSON, TEST_CHARGER)        //Move to constant
            );
        }
        catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new ResponseDto<>(
                    "FAILED_MESSAGE",
                    ErrorCodes.UNKNOWN_ERROR.getErrorCode(),
                    ErrorCodes.UNKNOWN_ERROR.getErrorMsg(),
                    null
            );
        }
    }

    @PostMapping(value = "/status-notification")
    public StatusNotificationResponse handleStatus(@RequestBody StatusNotificationRequest request){
        StatusNotificationResponse response = connectorService.statusNotification(request, TEST_CHARGER );
        return  response;
    }

    @PostMapping(value = "/heartbeat")
    public ResponseDto<HeartbeatResponse> handleHeartbeat(@RequestBody HeartbeatRequest request) {
        try {
            return new ResponseDto<>(
                    "SUCCESS_MESSAGE",
                    ErrorCodes.NO_ERROR.getErrorCode(),
                    ErrorCodes.NO_ERROR.getErrorMsg(),
                    chargerService.heartbeat(request, TEST_CHARGER)
            );
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new ResponseDto<>(
                    "FAILED_MESSAGE",
                    ErrorCodes.UNKNOWN_ERROR.getErrorCode(),
                    ErrorCodes.UNKNOWN_ERROR.getErrorMsg(),
                    null
            );
        }
    }

    @PostMapping(value = "/authorise")
    public ResponseDto<AuthorizeResponse> handleAuthorise(@RequestBody AuthorizeRequest request) {
        try {
            return new ResponseDto<>(
                    "SUCCESS_MESSAGE",
                    ErrorCodes.NO_ERROR.getErrorCode(),
                    ErrorCodes.NO_ERROR.getErrorMsg(),
                    ocppTagService.authorize(request, TEST_CHARGER)
            );
        }
        catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new ResponseDto<>(
                    "FAILED_MESSAGE",
                    ErrorCodes.UNKNOWN_ERROR.getErrorCode(),
                    ErrorCodes.UNKNOWN_ERROR.getErrorMsg(),
                    null
            );
        }
    }

    @PostMapping(value = "/start-transaction")
    public StartTransactionResponse handleStartTxn(@RequestBody StartTransactionRequest request){
        StartTransactionResponse response = transactionService.startTransaction(request, TEST_CHARGER);
        return response;
    }

    @PostMapping(value = "/stop-transaction")
    public StopTransactionResponse handleStopTxn(@RequestBody StopTransactionRequest request) {
        StopTransactionResponse response = transactionService.stopTransaction(request, TEST_CHARGER);
        return response;
    }


    @PostMapping(value = "/meter-value")
    public MeterValueResponse handleMeterValue(@RequestBody MeterValueRequest request ) {
        MeterValueResponse response = meterValueService.meterValue(request, TEST_CHARGER);
        return response;
    }


}





