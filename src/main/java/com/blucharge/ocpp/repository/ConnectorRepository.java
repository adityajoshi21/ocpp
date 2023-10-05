package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.ConnectorStatus;
import org.joda.time.DateTime;

public interface ConnectorRepository {

    Long getConnectorPkForChargeBoxAndConnectorNumber(Long chargerId, Integer connectorId);
    void updateConnectorStatus(StatusNotificationRequest statusNotificationRequest, Long chargerId);

    void updateConnectorState(Long transactionId, Long connectorPk, ConnectorState status);
    ConnectorRecord getConnectorForChargerIdWithConnectorNumber(Long chargerId, Integer connectorNo);
    Long addConnector(ConnectorRecord request);
    void updateConnectorStatus(Long connectorNo, DateTime timestamp, ConnectorStatus statusUpdate);
    ConnectorRecord getConnectorRecordFromConnectorId(Long connectorId, Long chargerId);

}
