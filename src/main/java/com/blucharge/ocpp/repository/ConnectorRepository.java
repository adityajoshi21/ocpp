package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ConnectorRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.ConnectorStatus;
import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import org.joda.time.DateTime;

import java.util.List;

public interface ConnectorRepository {

    Long getConnectorPkForChargeBoxAndConnector(Long chargerId,Integer connectorId);
    void updateConnectorStatus(StatusNotificationRequest p, Long chargerId);

Integer getConnectorNoFromConnectorId(Long ConnectorId);

    void updateConnectorState(Long transactionId, Long connectorPk, ConnectorState status);
    ConnectorRecord getConnectorForChargerIdWithConnectorNumber(Long chargerId, Integer connectorNo);
    Long addConnector(ConnectorRecord request);

    ConnectorRecord getConnectorFromConnectorNameAndChargerId(String connectorName, Long chargerId);
    void updateConnectorStatus(Long connectorPk, DateTime timestamp, ConnectorStatus statusUpdate);


//    String getConnectorStateByConnectorPk(Long connectorId);
//    Long getConnectorPkFromConnector(Long chargerId, Long connectorId);
//void insertIgnoreConnector(Long chargerId, Long connectorId);
//    List<ConnectorRecord> getAllConnectorsForChargerId(Long chargerId);
//    void insertConnector(ConnectorRequest connectorRequest);

}
