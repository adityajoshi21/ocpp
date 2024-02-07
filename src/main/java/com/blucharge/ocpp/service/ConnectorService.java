package com.blucharge.ocpp.service;

import com.blucharge.event.dto.ChangeConfigurationCommandDto;
import com.blucharge.event.dto.GetConfigurationCommandDto;
import com.blucharge.event.dto.TriggerMessageCommandDto;
import com.blucharge.event.dto.UnlockGunCommandDto;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;

public interface ConnectorService {
    StatusNotificationResponse insertStatusNotification(StatusNotificationRequest request, String chargerName);

    void unlockConnector(UnlockGunCommandDto unlockGunCommandDto);

    void triggerMessage(TriggerMessageCommandDto triggerMessageCommandDto);

    void getConfiguration(GetConfigurationCommandDto getConfigurationCommandDto);

    void changeConfiguration(ChangeConfigurationCommandDto changeConfigurationCommandDto);
}
