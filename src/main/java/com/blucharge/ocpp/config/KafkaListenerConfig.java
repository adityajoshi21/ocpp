package com.blucharge.ocpp.config;

import com.blucharge.event.dto.KafkaPublishEventDto;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.blucharge.event.constants.KafkaStringConstants.*;

@Component
public class KafkaListenerConfig {
    private static final Gson gson = new Gson();
    @KafkaListener(id = "DATA-EVENT", topics = {DATA_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void dataEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventType = eventDto.getEventType();
        String eventData = eventDto.getEventData().toString();
    }
    @KafkaListener(id = "COMMAND-EVENT", topics = {COMMAND_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void commandEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventType = eventDto.getEventType();
        String eventData = eventDto.getEventData().toString();
    }
    @KafkaListener(id = "CONNECTOR-EVENT", topics = {CONNECTOR_TOPIC_NAME}, containerFactory = "kafkaListenerContainerFactory", groupId = "OCPP")
    public void connectorEventListener(String message) {
        KafkaPublishEventDto eventDto = gson.fromJson(message, KafkaPublishEventDto.class);
        String eventType = eventDto.getEventType();
        String eventData = eventDto.getEventData().toString();
    }
}
