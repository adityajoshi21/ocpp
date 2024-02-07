package com.blucharge.ocpp.service;

import com.blucharge.event.dto.KafkaPublishEventDto;

public interface KafkaHelperService {
    void logEvent(KafkaPublishEventDto eventDto);
}
