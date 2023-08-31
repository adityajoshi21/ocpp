package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.api.UnlockConnectorRequest;
import com.blucharge.ocpp.dto.api.UnlockConnectorResponse;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationResponse;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.UnlockStatus;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.repository.TransactionsRepository;
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
    @Autowired
    private TransactionsRepository transactionsRepository;

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


    @Override
    public UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerIdentity) {
        //check if connector Exists?
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        Long id = charger.getId();
        ConnectorRecord connector = connectorRepository.getConnectorForChargerIdWithConnectorNumber(id, request.getConnectorId());
        Integer incomingConnectorId = connector.getConnectorNumber();
        if(Objects.isNull(incomingConnectorId)){
            log.error("ConnectorId not found for requested connector on which unlock connector command is sent");
        }
        else {
            //check if transaction is running on that connector?
           Boolean flag = transactionsRepository.isTransactionRunningOnConenctorId(connector.getId());
            UnlockConnectorResponse unlockConnectorResponse = new UnlockConnectorResponse();
            if(flag){
               log.error("Can't unlock connector as it has an active transaction");
                unlockConnectorResponse.setStatus(UnlockStatus.UNLOCK_FAILED);
               return unlockConnectorResponse;
           }
           else
               unlockConnectorResponse.setStatus(UnlockStatus.UNLOCKED);
            return unlockConnectorResponse;
        }
        return null;
    }
}