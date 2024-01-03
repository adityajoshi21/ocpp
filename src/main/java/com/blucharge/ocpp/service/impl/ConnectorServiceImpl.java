package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.event.dto.*;
import com.blucharge.event.enums.ConnectorEvent;
import com.blucharge.event.enums.KafkaEventType;
import com.blucharge.event.enums.KafkaTopic;
import com.blucharge.ocpp.config.KafkaConfiguration;
import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.dto.change_configuration.ChangeConfigurationRequest;
import com.blucharge.ocpp.dto.get_configuration.GetConfigurationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.dto.trigger_message.TriggerMessageRequest;
import com.blucharge.ocpp.dto.unlock_connector.UnlockConnectorRequest;
import com.blucharge.ocpp.enums.MessageTrigger;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepo;
import com.blucharge.ocpp.repository.EventRepo;
import com.blucharge.ocpp.service.ConnectorService;
import com.blucharge.util.utils.RandomUuidString;
import com.blucharge.util.utils.RequestContext;
import com.google.gson.Gson;
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
    @Autowired
    private KafkaConfiguration kafkaConfiguration;
    @Autowired
    private EventRepo eventRepo;

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
        // Info: publishing kafka connector status update event
        KafkaPublishEventDto<ConnectorStatusUpdateEventDto> eventDto = new KafkaPublishEventDto<>();
        eventDto.setTopic(KafkaTopic.CONNECTOR.name());
        eventDto.setEventUuid("EVT_"+ RandomUuidString.generateUuid());
        eventDto.setEventType(KafkaEventType.REQUEST.name());
        eventDto.setEventName(ConnectorEvent.STATUS.name());
        eventDto.setApplicationSourceId(ApplicationConstants.APPLICATION_ID);
        eventDto.setOrganisationId(RequestContext.getOrganizationId());
        eventDto.setCreatedBy("OCPP");
        eventDto.setEventData(new ConnectorStatusUpdateEventDto(
                connectorRecord.getUuid(),
                parameters.getStatus().name(),
                parameters.getTimestamp().getMillis()
        ));
        eventRepo.createRecordFromEvent(eventDto);
        kafkaConfiguration.kafkaTemplate().send(eventDto.getTopic(), new Gson().toJson(eventDto));

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