package com.blucharge.ocpp.contoller;

import com.blucharge.ocpp.dto.api.*;
import com.blucharge.ocpp.dto.authorize.AuthorizeRequest;
import com.blucharge.ocpp.dto.authorize.AuthorizeResponse;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationResponse;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatRequest;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatResponse;
import com.blucharge.ocpp.dto.meter_value.MeterValuesRequest;
import com.blucharge.ocpp.dto.meter_value.MeterValuesResponse;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionRequest;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionResponse;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionRequest;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionResponse;
import com.blucharge.ocpp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.blucharge.ocpp.constants.StringConstant.TEST_CHARGER;

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
    private MeterValuesService meterValueService;

    @PostMapping(value = "/boot-notification")
    public BootNotificationResponse handleBootNotification(@RequestBody BootNotificationRequest request) {
        // todo add kafka logic to onboard charger
        return chargerService.insertBootNotification(request, TEST_CHARGER);
    }

    @PostMapping(value = "/status-notification")
    public StatusNotificationResponse handleStatusNotification(@RequestBody StatusNotificationRequest request) {
        // todo add kafka logic to onboard connector
        return connectorService.insertStatusNotification(request, TEST_CHARGER);
    }

    @PostMapping(value = "/heartbeat")
    public HeartbeatResponse handleHeartbeat(@RequestBody HeartbeatRequest request) {
        return chargerService.insertHeartbeat(request, TEST_CHARGER);
    }

    @PostMapping(value = "/authorise")
    public AuthorizeResponse checkUserAuth(@RequestBody AuthorizeRequest request) {
        return ocppTagService.checkUserAuth(request, TEST_CHARGER);
    }

    @PostMapping(value = "/start-transaction")
    public StartTransactionResponse handleStartTxn(@RequestBody StartTransactionRequest request) {
        return transactionService.startTransaction(request, TEST_CHARGER);
    }

    @PostMapping(value = "/meter-value")
    public MeterValuesResponse handleMeterValue(@RequestBody MeterValuesRequest request) {
        return meterValueService.insertMeterValue(request, TEST_CHARGER);
    }

    @PostMapping(value = "/stop-transaction")
    public StopTransactionResponse handleStopTxn(@RequestBody StopTransactionRequest request) {
        return transactionService.stopTransaction(request, TEST_CHARGER);
    }

    @GetMapping(value = "/get-config")
    public GetConfigResponse handleGetConfig(@RequestBody GetConfigRequest request) {
        // todo need to be made event based
        return chargerService.getConfiguration(request, TEST_CHARGER);
    }

    @PostMapping(value = "/change-config")
    public ChangeConfigResponse handleChangeConfig(@RequestBody ChangeConfigRequest request) {
        // todo need to be made event based
        return chargerService.changeConfiguration(request, TEST_CHARGER);
    }

    @PostMapping(value = "/trigger-message")
    public TriggerMessageResponse handleTriggerMessage(@RequestBody TriggerMessageRequest request) {
        // todo need to be made event based
        return chargerService.triggerMessage(request, TEST_CHARGER);
    }

    @PostMapping(value = "/unlock-connector")
    public UnlockConnectorResponse handleUnlockConnector(@RequestBody UnlockConnectorRequest unlockConnectorRequest) {
        // todo need to be made event based
        return connectorService.unlockConnector(unlockConnectorRequest, TEST_CHARGER);
    }

}

