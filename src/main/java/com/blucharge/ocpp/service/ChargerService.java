package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationResponse;
import com.blucharge.ocpp.dto.ws.HeartbeatRequest;
import com.blucharge.ocpp.dto.ws.HeartbeatResponse;
import com.blucharge.ocpp.enums.OcppProtocol;

public interface ChargerService {
    BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargerIdentity);
    HeartbeatResponse heartbeat(HeartbeatRequest request, String chargerIdentity);
    boolean isRegistered(String chargerId);
}
