package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.event.dto.ChangeConfigurationCommandDto;
import com.blucharge.event.dto.GetConfigurationCommandDto;
import com.blucharge.event.dto.TriggerMessageCommandDto;
import com.blucharge.event.dto.UnlockGunCommandDto;
import com.blucharge.ocpp.dto.change_configuration.ChangeConfigurationRequest;
import com.blucharge.ocpp.dto.get_configuration.GetConfigurationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.dto.trigger_message.TriggerMessageRequest;
import com.blucharge.ocpp.dto.unlock_connector.UnlockConnectorRequest;
import com.blucharge.ocpp.enums.MessageTrigger;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepo;
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
    private ConnectorRepo connectorRepo;
    @Autowired
    private ChargerRepo chargerRepo;

    @Override
    public StatusNotificationResponse insertStatusNotification(StatusNotificationRequest parameters, String chargerName) {
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordFromName(chargerName);
        if (Objects.isNull(chargerRecord)) {
            return new StatusNotificationResponse();
        }
        if (parameters.getConnectorId() == 0) {
            chargerRepo.updateChargerHeartBeat(chargerRecord.getId(), DateTime.now());
            return new StatusNotificationResponse();
        }
        ConnectorRecord connectorRecord = connectorRepo.getConnectorRecordForChargerIdAndConnectorNumber(chargerRecord.getId(), parameters.getConnectorId());
        if (Objects.isNull(connectorRecord)) {
            return new StatusNotificationResponse();
        }
        connectorRepo.updateConnectorStatus(parameters, connectorRecord.getId());
        return new StatusNotificationResponse();
    }

    @Override
    public void unlockConnector(UnlockGunCommandDto unlockGunCommandDto) {
        ConnectorRecord connectorRecord = connectorRepo.getConnectorRecordFromUuid(unlockGunCommandDto.getConnectorUuid());
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(connectorRecord.getChargerId());
        UnlockConnectorRequest unlockConnectorRequest = new UnlockConnectorRequest();
        unlockConnectorRequest.setConnectorId(connectorRecord.getNumber());
        String chargerName = chargerRecord.getName();
        // todo sent this dto over socket session
    }

    @Override
    public void triggerMessage(TriggerMessageCommandDto triggerMessageCommandDto) {
        ConnectorRecord connectorRecord = connectorRepo.getConnectorRecordFromUuid(triggerMessageCommandDto.getConnectorUuid());
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(connectorRecord.getChargerId());
        TriggerMessageRequest triggerMessageRequest = new TriggerMessageRequest();
        triggerMessageRequest.setConnectorId(connectorRecord.getNumber());
        triggerMessageRequest.setRequestedMessage(MessageTrigger.valueOf(triggerMessageCommandDto.getRequestedMessage()));
        String chargerName = chargerRecord.getName();
        // todo sent this dto over socket session
    }

    @Override
    public void getConfiguration(GetConfigurationCommandDto getConfigurationCommandDto) {
        ConnectorRecord connectorRecord = connectorRepo.getConnectorRecordFromUuid(getConfigurationCommandDto.getConnectorUuid());
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(connectorRecord.getChargerId());
        GetConfigurationRequest getConfigurationRequest = new GetConfigurationRequest();
        getConfigurationRequest.setKey(getConfigurationCommandDto.getConfigurationKeyString());
        String chargerName = chargerRecord.getName();
        // todo sent this dto over socket session
    }

    @Override
    public void changeConfiguration(ChangeConfigurationCommandDto changeConfigurationCommandDto) {
        ConnectorRecord connectorRecord = connectorRepo.getConnectorRecordFromUuid(changeConfigurationCommandDto.getConnectorUuid());
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(connectorRecord.getChargerId());
        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest();
        changeConfigurationRequest.setKey(changeConfigurationCommandDto.getConfigurationKey());
        changeConfigurationRequest.setValue(changeConfigurationCommandDto.getNewValue());
        String chargerName = chargerRecord.getName();
        // todo sent this dto over socket session
    }

}