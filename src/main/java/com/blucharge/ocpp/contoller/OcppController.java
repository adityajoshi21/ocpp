package com.blucharge.ocpp.contoller;

import com.blucharge.ocpp.dto.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.enums.OcppProtocol;
import com.blucharge.ocpp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.blucharge.ocpp.constants.ApplicationConstants.TEST_CHARGER;

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
    public BootNotificationResponse handleBootNotification(@RequestBody BootNotificationRequest request) {
        return chargerService.bootNotification(request, OcppProtocol.V_16_JSON, TEST_CHARGER);
    }

    @PostMapping(value = "/status-notification")
    public StatusNotificationResponse handleStatus(@RequestBody StatusNotificationRequest request){
        return connectorService.statusNotification(request, TEST_CHARGER );
    }

    @PostMapping(value = "/heartbeat")
    public HeartbeatResponse handleHeartbeat(@RequestBody HeartbeatRequest request) {
        return chargerService.heartbeat(request, TEST_CHARGER);
    }

    @PostMapping(value = "/authorise")
    public AuthorizeResponse handleAuthorise(@RequestBody AuthorizeRequest request) {
          return ocppTagService.authorize(request, TEST_CHARGER);
    }

    @PostMapping(value = "/start-transaction")
    public StartTransactionResponse handleStartTxn(@RequestBody StartTransactionRequest request){
        return transactionService.startTransaction(request, TEST_CHARGER);
    }

    @PostMapping(value = "/stop-transaction")
    public StopTransactionResponse handleStopTxn(@RequestBody StopTransactionRequest request) {
        return transactionService.stopTransaction(request, TEST_CHARGER);
    }


    @PostMapping(value = "/meter-value")
    public MeterValueResponse handleMeterValue(@RequestBody MeterValueRequest request ) {
        return meterValueService.meterValue(request, TEST_CHARGER);
    }

    @PostMapping(value = "/remote-start-transaction")
    public RemoteStartTransactionResponse handleRemoteStart(@RequestBody RemoteStartTransactionRequest request ) {
        return transactionService.remoteStart(request, TEST_CHARGER);
    }

    @PostMapping(value = "/remote-stop-transaction")
    public RemoteStopTransactionResponse handleRemoteStop(@RequestBody RemoteStopTransactionRequest request ) {
        return transactionService.remoteStop(request, TEST_CHARGER);
    }

}





