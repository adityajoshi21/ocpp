package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationResponse;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatRequest;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatResponse;

public interface ChargerService {
    BootNotificationResponse insertBootNotification(BootNotificationRequest bootNotificationRequest, String chargerName);

    HeartbeatResponse insertHeartbeat(HeartbeatRequest heartbeatRequest, String chargerName);

    Boolean isChargerRegistered(String chargerId);
}
