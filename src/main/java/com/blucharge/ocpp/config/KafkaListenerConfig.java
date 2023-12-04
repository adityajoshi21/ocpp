package com.blucharge.ocpp.config;

import com.blucharge.event.dto.*;
import com.blucharge.ocpp.service.ConnectorService;
import com.blucharge.ocpp.service.TransactionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.blucharge.event.constants.KafkaStringConstants.*;

@Component
public class KafkaListenerConfig {

    private static final Gson gson = new Gson();
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ConnectorService connectorService;

    @KafkaListener(id = "DATA-EVENT", topics = {DATA_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void dataEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventType = eventDto.getEventType();
        String eventData = eventDto.getEventData().toString();
    }

    @KafkaListener(id = "COMMAND-EVENT", topics = {COMMAND_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void commandEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventName = eventDto.getEventName();
        String eventData = eventDto.getEventData().toString();
        if (REMOTE_START_COMMAND_EVENT_NAME.equals(eventName)) {
            RemoteStartCommandDto remoteStartCommandDto = gson.fromJson(eventData, RemoteStartCommandDto.class);
            transactionService.handleRemoteStartCommand(remoteStartCommandDto);
        } else if (REMOTE_START_COMMAND_EVENT_NAME.equals(eventName)) {
            RemoteStopCommandDto remoteStopCommandDto = gson.fromJson(eventData, RemoteStopCommandDto.class);
            transactionService.handleRemoteStopCommand(remoteStopCommandDto);
        } else if (UNLOCK_GUN_COMMAND_EVENT_NAME.equals(eventName)) {
            UnlockGunCommandDto unlockGunCommandDto = gson.fromJson(eventData, UnlockGunCommandDto.class);
            connectorService.unlockConnector(unlockGunCommandDto);
        } else if (TRIGGER_MESSAGE_COMMAND_EVENT_NAME.equals(eventName)) {
            TriggerMessageCommandDto triggerMessageCommandDto = gson.fromJson(eventData, TriggerMessageCommandDto.class);
            connectorService.triggerMessage(triggerMessageCommandDto);
        } else if (GET_CONFIGURATION_COMMAND_EVENT_NAME.equals(eventName)) {
            GetConfigurationCommandDto getConfigurationCommandDto = gson.fromJson(eventData, GetConfigurationCommandDto.class);
            connectorService.getConfiguration(getConfigurationCommandDto);
        } else if (CHANGE_CONFIGURATION_COMMAND_EVENT_NAME.equals(eventName)) {
            ChangeConfigurationCommandDto changeConfigurationCommandDto = gson.fromJson(eventData, ChangeConfigurationCommandDto.class);
            connectorService.changeConfiguration(changeConfigurationCommandDto);
        }
    }

    @KafkaListener(id = "CONNECTOR-EVENT", topics = {CONNECTOR_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void connectorEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventType = eventDto.getEventType();
        String eventData = eventDto.getEventData().toString();
    }
}
