package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.GetConfigRequest;
import com.blucharge.ocpp.dto.GetConfigResponse;
import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationResponse;
import com.blucharge.ocpp.dto.ws.HeartbeatRequest;
import com.blucharge.ocpp.dto.ws.HeartbeatResponse;
import com.blucharge.ocpp.enums.OcppProtocol;

public interface ChargerService {
    BootNotificationResponse bootNotification(BootNotificationRequest bootNotificationRequest, String chargerIdentity);
    HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest, String chargerIdentity);
    boolean isRegistered(String chargerId);
    GetConfigResponse getConfiguration (GetConfigRequest getConfigRequest, String chargerIdentity);
}
