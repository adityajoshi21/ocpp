package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.EventRecord;
import com.blucharge.event.dto.KafkaPublishEventDto;

public interface EventRepo {
    void createRecordFromEvent(KafkaPublishEventDto eventDto);

    void createRecord(EventRecord eventRecord);

    void updateAckForEventUuid(KafkaPublishEventDto event);
}
