package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import org.joda.time.DateTime;

import java.util.List;

public interface ConnectorRepository {
    void updateConnectorStatus(StatusNotificationRequest statusNotificationRequest, Long chargerId);

    ConnectorRecord getConnectorRecordForChargerIdAndConnectorNumber(Long chargerId, Integer connectorNumber);

    List<ConnectorRecord> getConnectorRecordForChargerId(Long chargerId);

    void updateConnectorHeartBeat(Long connectorId, DateTime dateTime);

    ConnectorRecord getConnectorFromUuid(String connectorId);
}
