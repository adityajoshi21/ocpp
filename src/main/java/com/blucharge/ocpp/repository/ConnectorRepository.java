package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.ConnectorStatus;
import org.joda.time.DateTime;

import java.util.List;

public interface ConnectorRepository {
    void updateConnectorStatus(StatusNotificationRequest statusNotificationRequest, Long chargerId);
    void updateConnectorState(Long transactionId, Long connectorPk, ConnectorState status);
    ConnectorRecord getConnectorRecordForChargerIdAndConnectorNumber(Long chargerId, Integer connectorNumber);
    void updateConnectorStatus(Long connectorNo, DateTime timestamp, ConnectorStatus statusUpdate);
    ConnectorRecord getConnectorRecordFromConnectorId(Long connectorId, Long chargerId);
    List<ConnectorRecord> getConnectorRecordForChargerId(Long chargerId);

    void updateConnectorHeartBeat(Long connectorId, DateTime dateTime);
}
