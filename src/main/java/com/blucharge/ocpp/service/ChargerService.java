package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.api.*;
import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationResponse;
import com.blucharge.ocpp.dto.ws.HeartbeatRequest;
import com.blucharge.ocpp.dto.ws.HeartbeatResponse;

public interface ChargerService {
    BootNotificationResponse bootNotification(BootNotificationRequest bootNotificationRequest, String chargerIdentity);
    HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest, String chargerIdentity);
    Boolean isRegistered(String chargerId);
    GetConfigResponse getConfiguration (GetConfigRequest getConfigRequest, String chargerIdentity);
    ChangeConfigResponse changeConfiguration(ChangeConfigRequest changeConfigRequest, String chargerIdentity);
    TriggerMessageResponse triggerMessage(TriggerMessageRequest triggerMessageRequest, String chargerIdentity);
}
