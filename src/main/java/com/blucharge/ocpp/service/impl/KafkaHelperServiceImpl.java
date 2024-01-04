package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.EventRecord;
import com.blucharge.event.dto.KafkaPublishEventDto;
import com.blucharge.event.enums.KafkaEventType;
import com.blucharge.ocpp.config.KafkaConfiguration;
import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.repository.EventRepo;
import com.blucharge.ocpp.service.KafkaHelperService;
import com.blucharge.util.utils.RandomUuidString;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaHelperServiceImpl implements KafkaHelperService {
    @Autowired
    private KafkaConfiguration kafkaConfiguration;
    @Autowired
    private EventRepo eventRepo;
    @Override
    public void logEvent(KafkaPublishEventDto eventDto) {
        String eventType = eventDto.getEventType();
        if (KafkaEventType.RESPONSE.name().equals(eventType)) {
            eventRepo.updateAckForEventUuid(eventDto);
            return;
        }
        EventRecord eventRecord = new EventRecord();
        eventRecord.setData(eventDto.getEventData().toString());
        eventRecord.setApiSource(eventDto.getApplicationSourceId());
        eventRecord.setCreatedBy(eventDto.getCreatedBy());
        eventRecord.setOrganisationId(eventDto.getOrganisationId());
        eventRecord.setExtEventUuid(eventDto.getEventUuid());
        eventRecord.setUuid("EVT_"+RandomUuidString.generateUuid());
        eventRecord.setTopic(eventDto.getTopic());
        eventRecord.setType(eventDto.getEventType());
        eventRecord.setName(eventDto.getEventName());
        eventRepo.createRecord(eventRecord);
        if (KafkaEventType.REQUEST.name().equals(eventType)){
            KafkaPublishEventDto eventDto1 = new KafkaPublishEventDto<>();
            eventDto1.setEventUuid(eventDto.getEventUuid());
            eventDto1.setEventType(KafkaEventType.RESPONSE.name());
            eventDto1.setEventName(eventDto.getEventName());
            eventDto1.setTopic(eventDto.getTopic());
            eventDto1.setApplicationSourceId(ApplicationConstants.APPLICATION_ID);
            eventDto1.setCreatedBy(ApplicationConstants.APPLICATION_ID);
            eventDto1.setOrganisationId(eventDto1.getOrganisationId());
            kafkaConfiguration.kafkaTemplate().send(eventDto1.getTopic(),new Gson().toJson(eventDto1));
        }
    }
}
