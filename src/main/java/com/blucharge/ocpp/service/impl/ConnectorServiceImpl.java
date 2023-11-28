package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.api.UnlockConnectorRequest;
import com.blucharge.ocpp.dto.api.UnlockConnectorResponse;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.enums.RegistrationStatus;
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
    public StatusNotificationResponse insertStatusNotification(StatusNotificationRequest parameters, String chargerName){
        ChargerRecord chargerRecord = chargerRepository.getChargerRecordFromName(chargerName);
        if(Objects.isNull(chargerRecord)) {
            return new StatusNotificationResponse(RegistrationStatus.REJECTED.name());
        }
        if(parameters.getConnectorId() == 0){
            chargerRepository.updateChargerHeartBeat(chargerName, DateTime.now());
            return new StatusNotificationResponse(RegistrationStatus.ACCEPTED.name());
        }
        ConnectorRecord connectorRecord = connectorRepository.getConnectorRecordForChargerIdAndConnectorNumber(chargerRecord.getId(),parameters.getConnectorId());
        if(Objects.isNull(connectorRecord)) {
            return new StatusNotificationResponse(RegistrationStatus.REJECTED.name());
        }
        connectorRepository.updateConnectorStatus(parameters, chargerRecord.getId());
        // todo add kafka event for connector status
        return new StatusNotificationResponse(RegistrationStatus.ACCEPTED.name());
    }


    @Override
    public UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerName) {
        //check if connector Exists?
        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerName).get(0);
        Long id = null;
        if(!Objects.isNull(charger)){
        id = charger.getId();}
        ConnectorRecord connectorRecord = connectorRepository.getConnectorRecordForChargerIdAndConnectorNumber(id, request.getConnectorId());

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