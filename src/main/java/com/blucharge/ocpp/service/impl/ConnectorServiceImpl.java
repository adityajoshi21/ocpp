package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.event.dto.ConnectorStatusUpdateEventDto;
import com.blucharge.event.dto.KafkaPublishEventDto;
import com.blucharge.ocpp.config.KafkaConfiguration;
import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.dto.api.UnlockConnectorRequest;
import com.blucharge.ocpp.dto.api.UnlockConnectorResponse;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.enums.RegistrationStatus;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.repository.EventRepo;
import com.blucharge.ocpp.service.ConnectorService;
import com.blucharge.util.utils.RequestContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.blucharge.event.constants.KafkaStringConstants.*;


@Slf4j
@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;
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
            return new StatusNotificationResponse(RegistrationStatus.REJECTED.name());
        }
        if (parameters.getConnectorId() == 0) {
            chargerRepo.updateChargerHeartBeat(chargerRecord.getId(), DateTime.now());
            return new StatusNotificationResponse(RegistrationStatus.ACCEPTED.name());
        }
        ConnectorRecord connectorRecord = connectorRepository.getConnectorRecordForChargerIdAndConnectorNumber(chargerRecord.getId(), parameters.getConnectorId());
        if (Objects.isNull(connectorRecord)) {
            return new StatusNotificationResponse(RegistrationStatus.REJECTED.name());
        }
        connectorRepository.updateConnectorStatus(parameters, connectorRecord.getId());
        // Info: kafka connector status update event
        KafkaPublishEventDto<ConnectorStatusUpdateEventDto> eventDto = new KafkaPublishEventDto<>();
        eventDto.setTopic(COMMAND_TOPIC_NAME);
        eventDto.setEventType(REQUEST_EVENT_TYPE_NAME);
        eventDto.setEventName(CONNECTOR_STATUS_EVENT_NAME);
        eventDto.setApplicationSourceId(ApplicationConstants.APPLICATION_ID);
        eventDto.setOrganisationId(RequestContext.getOrganizationId());
        eventDto.setCreatedBy("OCPP");
        eventDto.setEventData(new ConnectorStatusUpdateEventDto(
                connectorRecord.getUuid(),
                parameters.getStatus().name(),
                parameters.getTimestamp().getMillis()
        ));
        eventRepo.createRecord(eventDto);
        kafkaConfiguration.kafkaTemplate().send(eventDto.getTopic(), new Gson().toJson(eventDto));

        return new StatusNotificationResponse(RegistrationStatus.ACCEPTED.name());
    }


    @Override
    public UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerName) {
        return new UnlockConnectorResponse();
    }
}