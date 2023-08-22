package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationResponse;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.service.ConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;



@Slf4j
@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private ChargerRepository chargerRepository;

    @Override
    public StatusNotificationResponse statusNotification(StatusNotificationRequest parameters, String chargerIdentity){


        ChargerRecord charger =chargerRepository.getChargerFromChargerId(chargerIdentity);   //Exception handler needed if charger is null & charger doesnt exists in table

        Integer noOfConnectors = charger.getNoOfConnectors();

        if(parameters.getConnectorId() == 0){       // if it is 0; update heartbeat in Charger Table and move on
            log.info("No of connectors for Charger Id {} , found to be 0 updating heartbeat", chargerIdentity);
            chargerRepository.updateChargerHeartbeat(chargerIdentity, DateTime.now());
            return new StatusNotificationResponse("Success");
        }
        ConnectorRecord connector = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(),parameters.getConnectorId());

        if(Objects.isNull(connector))   //Connector doesn't exist in Connector Table
        {
            ConnectorRecord connectorRecord = new ConnectorRecord();
            connectorRecord.setChargerId(charger.getId());
            connectorRecord.setConnectorNumber(parameters.getConnectorId());
            connectorRecord.setState(ConnectorState.IDLE.name()); // do we need this
            connectorRepository.addConnector(connectorRecord);
            chargerRepository.updateNumberOfConnectors(charger.getId(), noOfConnectors+1);
        }
        connectorRepository.updateConnectorStatus(parameters, charger.getId());

        return new StatusNotificationResponse("Success");
    }

}
