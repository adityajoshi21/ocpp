package com.blucharge.ocpp.repository;


import com.blucharge.event.dto.KafkaPublishEventDto;

public interface EventRepo {
    void createRecord(KafkaPublishEventDto eventDto);
}
