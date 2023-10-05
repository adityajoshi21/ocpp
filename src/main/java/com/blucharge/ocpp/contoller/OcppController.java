package com.blucharge.ocpp.contoller;

import com.blucharge.ocpp.dto.api.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.api.*;
import com.blucharge.ocpp.dto.ws.*;
import com.blucharge.ocpp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return chargerService.bootNotification(request,  TEST_CHARGER);
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
    public StartTransactionResponse handleStartTxn(@RequestBody  StartTransactionRequest request){
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
        return transactionService.remoteStartTransaction(request, TEST_CHARGER);
    }

    @PostMapping(value = "/remote-stop-transaction")
    public RemoteStopTransactionResponse handleRemoteStop(@RequestBody RemoteStopTransactionRequest request ) {
        return transactionService.remoteStopTransaction(request, TEST_CHARGER);
    }

    @GetMapping(value = "/get-config")
    public GetConfigResponse handleGetConfig(@RequestBody GetConfigRequest request) {
        return chargerService.getConfiguration(request, TEST_CHARGER);
    }

    @PostMapping(value = "/change-config")
    public ChangeConfigResponse handleChangeConfig(@RequestBody ChangeConfigRequest request) {
        return chargerService.changeConfiguration(request, TEST_CHARGER);
    }

    @PostMapping(value = "/trigger-message")
    public TriggerMessageResponse handleTriggerMessage(@RequestBody TriggerMessageRequest request){
        return chargerService.triggerMessage(request, TEST_CHARGER);
    }

    @PostMapping(value = "/unlock-connector")
    public UnlockConnectorResponse handleUnlockConnector(@RequestBody UnlockConnectorRequest unlockConnectorRequest){
        return connectorService.unlockConnector(unlockConnectorRequest, TEST_CHARGER);
    }

}

