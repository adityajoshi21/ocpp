package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ConnectorRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import org.joda.time.DateTime;

import java.util.List;

public interface ConnectorRepository {

    Long getConnectorPkForChargeBoxAndConnector(Long chargerId,Integer connectorId);
    void updateConnectorStatus(StatusNotificationRequest p, Long chargerId);
    Long getConnectorPkFromConnector(Long chargerId, Long connectorId);
    String getConnectorStateByConnectorPk(Long connectorId);
    void updateConnectorStateToIdle(Long connectorPk);
    void updateConnectorState(Long transactionId, Long connectorPk, ConnectorState status);

    ConnectorRecord getConnectorForChargerIdWithConnectorNumber(Long chargerId, Integer connectorNo);
    List<ConnectorRecord> getAllConnectorsForChargerId(Long chargerId);
    Long addConnector(ConnectorRecord request);
    //void insertIgnoreConnector(Long chargerId, Long connectorId);

    void insertConnector(ConnectorRequest connectorRequest);
    Integer getConnectorNoFromConnectorId(Long ConnectorId);
    void insertConnectorStatus(Long connectorPk, DateTime timestamp, TransactionStatusUpdate statusUpdate);
    ConnectorRecord getConnectorFromConnectorNameAndChargerId(String connectorName, Long chargerId);


}
