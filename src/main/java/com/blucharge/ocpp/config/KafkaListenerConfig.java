package com.blucharge.ocpp.config;

import com.blucharge.event.dto.*;
import com.blucharge.event.enums.ConnectorEvent;
import com.blucharge.ocpp.service.ConnectorService;
import com.blucharge.ocpp.service.KafkaHelperService;
import com.blucharge.ocpp.service.TransactionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerConfig {
    private static final Gson gson = new Gson();
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private KafkaHelperService kafkaHelperService;

    @KafkaListener(id = "COMMAND-EVENT", topics = {"COMMAND-EVENT"}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void commandEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventName = eventDto.getEventName();
        String eventData = eventDto.getEventData().toString();
        kafkaHelperService.logEvent(eventDto);
        if (ConnectorEvent.REMOTE_START.name().equals(eventName)) {
            RemoteStartCommandDto remoteStartCommandDto = gson.fromJson(eventData, RemoteStartCommandDto.class);
            transactionService.handleRemoteStartCommand(remoteStartCommandDto);
        } else if (ConnectorEvent.REMOTE_STOP.name().equals(eventName)) {
            RemoteStopCommandDto remoteStopCommandDto = gson.fromJson(eventData, RemoteStopCommandDto.class);
            transactionService.handleRemoteStopCommand(remoteStopCommandDto);
        } else if (ConnectorEvent.UNLOCK_GUN.name().equals(eventName)) {
            UnlockGunCommandDto unlockGunCommandDto = gson.fromJson(eventData, UnlockGunCommandDto.class);
            connectorService.unlockConnector(unlockGunCommandDto);
        } else if (ConnectorEvent.TRIGGER_MESSAGE.name().equals(eventName)) {
            TriggerMessageCommandDto triggerMessageCommandDto = gson.fromJson(eventData, TriggerMessageCommandDto.class);
            connectorService.triggerMessage(triggerMessageCommandDto);
        } else if (ConnectorEvent.GET_CONFIGURATION.name().equals(eventName)) {
            GetConfigurationCommandDto getConfigurationCommandDto = gson.fromJson(eventData, GetConfigurationCommandDto.class);
            connectorService.getConfiguration(getConfigurationCommandDto);
        } else if (ConnectorEvent.CHANGE_CONFIGURATION.name().equals(eventName)) {
            ChangeConfigurationCommandDto changeConfigurationCommandDto = gson.fromJson(eventData, ChangeConfigurationCommandDto.class);
            connectorService.changeConfiguration(changeConfigurationCommandDto);
        }
    }
}
