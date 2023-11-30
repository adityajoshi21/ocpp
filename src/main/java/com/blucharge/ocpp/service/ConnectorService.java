package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.api.UnlockConnectorRequest;
import com.blucharge.ocpp.dto.api.UnlockConnectorResponse;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;

public interface ConnectorService {
    StatusNotificationResponse insertStatusNotification(StatusNotificationRequest request, String chargerName);

    UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerName);
}
