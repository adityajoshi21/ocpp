package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationResponse;

public interface ConnectorService {
    StatusNotificationResponse statusNotification(StatusNotificationRequest request, String chargerIdentity);
}
