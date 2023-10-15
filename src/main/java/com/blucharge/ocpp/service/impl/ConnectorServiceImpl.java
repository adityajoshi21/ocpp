package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
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

        ChargerRecord chargerRecord = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);   //Exception handler needed if charger record is null i.e. charger doesn't exist in table
        if(!Objects.isNull(chargerRecord)){
        Integer noOfConnectors = chargerRecord.getNoOfConnectors();
        if(parameters.getConnectorId() == 0){       // if it is 0; update heartbeat in Charger Table and move on
            log.info("No of connectors for Charger Id {} , found to be 0 updating heartbeat", chargerIdentity);
            chargerRepository.updateChargerHeartbeat(chargerIdentity, DateTime.now());
            return new StatusNotificationResponse("Connector ID was 0");
        }
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(chargerRecord.getId(),parameters.getConnectorId());

        if(Objects.isNull(connectorRecord))   //Connector doesn't exist in Connector Table
        {
            ConnectorRecord record = new ConnectorRecord();
            record.setChargerId(chargerRecord.getId());
            record.setConnectorNumber(parameters.getConnectorId());
            record.setState(ConnectorState.IDLE.name());
            connectorRepository.addConnector(record);
            chargerRepository.updateNumberOfConnectors(chargerRecord.getId(), noOfConnectors+1);
        }
        connectorRepository.updateConnectorStatus(parameters, chargerRecord.getId());
        }

        return new StatusNotificationResponse("{}");

    }


    @Override
    public UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerIdentity) {
        //check if connector Exists?
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity).get(0);
        Long id = null;
        if(!Objects.isNull(charger)){
        id = charger.getId();}
        ConnectorRecord connectorRecord = connectorRepository.getConnectorForChargerIdWithConnectorNumber(id, request.getConnectorId());

        if(Objects.isNull(connectorRecord)){
            log.error("ConnectorI not found on which unlock connector command is sent");
            UnlockConnectorResponse response = new UnlockConnectorResponse();
            response.setStatus(UnlockStatus.UNLOCK_FAILED);
            return  response;
        }
        else {
            //check if transaction is running on that connector?
            TransactionRecord transactionRecord = transactionsRepository.getActiveTransactionOnConnectorId(request.getConnectorId(), charger.getId());
            UnlockConnectorResponse unlockConnectorResponse = new UnlockConnectorResponse();
            if(!Objects.isNull(transactionRecord)){
               log.error("Can't unlock connector as it has an active transaction");
                unlockConnectorResponse.setStatus(UnlockStatus.UNLOCK_FAILED);
               return unlockConnectorResponse;
           }
           else
               unlockConnectorResponse.setStatus(UnlockStatus.UNLOCKED);
            return unlockConnectorResponse;
        }
    }
}