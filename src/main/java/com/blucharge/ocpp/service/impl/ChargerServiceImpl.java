package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.event.dto.HeartBeatEventDto;
import com.blucharge.event.dto.KafkaPublishEventDto;
import com.blucharge.ocpp.config.KafkaConfiguration;
import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationResponse;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatRequest;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatResponse;
import com.blucharge.ocpp.enums.RegistrationStatus;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepo;
import com.blucharge.ocpp.repository.EventRepo;
import com.blucharge.ocpp.service.ChargerService;
import com.blucharge.util.utils.RequestContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.blucharge.event.constants.KafkaStringConstants.*;
import static com.blucharge.ocpp.constants.StringConstant.HEART_BEAT_INTERVAL;


@Slf4j
@Service
public class ChargerServiceImpl implements ChargerService {
    @Autowired
    private ChargerRepo chargerRepo;

    @Autowired
    private ConnectorRepo connectorRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Override
    public BootNotificationResponse insertBootNotification(BootNotificationRequest parameters, String chargerName) {
        Boolean isRegistered = isChargerRegistered(chargerName);

        if (Boolean.TRUE.equals(isRegistered)) {
            chargerRepo.updateBootNotificationForCharger(parameters, chargerName);
        } else {
            log.info("The charger is not registered with blucharge" + chargerName);
        }

        return new BootNotificationResponse(
                DateTime.now(),
                HEART_BEAT_INTERVAL,
                Boolean.TRUE.equals(isRegistered) ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED
        );
    }

    @Override
    public HeartbeatResponse insertHeartbeat(HeartbeatRequest request, String chargerName) {
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordFromName(chargerName);
        chargerRepo.updateChargerHeartBeat(chargerRecord.getId(), DateTime.now());
        List<ConnectorRecord> connectorRecords = connectorRepo.getConnectorRecordForChargerId(chargerRecord.getId());
        for (ConnectorRecord connectorRecord : connectorRecords) {
            connectorRepo.updateConnectorHeartBeat(connectorRecord.getId(), DateTime.now());
            // Info: kafka connector heart beat update event
            KafkaPublishEventDto<HeartBeatEventDto> eventDto = new KafkaPublishEventDto<>();
            eventDto.setTopic(DATA_TOPIC_NAME);
            eventDto.setEventType(DATA_EVENT_TYPE_NAME);
            eventDto.setEventName(CONNECTOR_HEART_BEAT_EVENT_NAME);
            eventDto.setApplicationSourceId(ApplicationConstants.APPLICATION_ID);
            eventDto.setOrganisationId(RequestContext.getOrganizationId());
            eventDto.setCreatedBy("OCPP");
            eventDto.setEventData(new HeartBeatEventDto(
                    connectorRecord.getUuid(),
                    DateTime.now().getMillis()
            ));
            eventRepo.createRecord(eventDto);
            kafkaConfiguration.kafkaTemplate().send(eventDto.getTopic(), new Gson().toJson(eventDto));
        }
        return new HeartbeatResponse(DateTime.now());
    }

    @Override
    public Boolean isChargerRegistered(String chargerName) {
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordFromName(chargerName);
        return !Objects.isNull(chargerRecord);
    }
}

