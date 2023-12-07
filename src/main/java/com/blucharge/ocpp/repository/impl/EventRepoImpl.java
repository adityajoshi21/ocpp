package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Event;
import com.blucharge.db.ocpp.tables.records.EventRecord;
import com.blucharge.event.dto.KafkaPublishEventDto;
import com.blucharge.ocpp.repository.EventRepo;
import com.google.gson.Gson;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepoImpl implements EventRepo {
    private static final Event event = Event.EVENT;
    @Autowired
    private DSLContext ctx;

    public EventRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void createRecord(KafkaPublishEventDto eventDto) {
        EventRecord eventRecord = new EventRecord();
        eventRecord.setUuid(eventDto.getEventUuid());
        eventRecord.setTopic(eventDto.getTopic());
        eventRecord.setType(eventDto.getEventType());
        eventRecord.setName(eventDto.getEventName());
        eventRecord.setApiSource(eventDto.getApplicationSourceId());
        eventRecord.setCreatedBy(eventDto.getCreatedBy());
        eventRecord.setUpdatedBy(eventDto.getCreatedBy());
        eventRecord.setIsActive(true);
        eventRecord.setOrganisationId(eventDto.getOrganisationId());
        eventRecord.setData(new Gson().toJson(eventDto.getEventData()));
        EventRecord eventRecord1 = ctx.newRecord(event, eventRecord);
        eventRecord1.store();
    }
}
