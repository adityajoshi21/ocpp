package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationResponse;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatRequest;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatResponse;
import com.blucharge.ocpp.enums.RegistrationStatus;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepo;
import com.blucharge.ocpp.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.blucharge.ocpp.constants.StringConstant.HEART_BEAT_INTERVAL;


@Slf4j
@Service
public class ChargerServiceImpl implements ChargerService {
    @Autowired
    private ChargerRepo chargerRepo;

    @Autowired
    private ConnectorRepo connectorRepo;

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
        }
        return new HeartbeatResponse(DateTime.now());
    }

    @Override
    public Boolean isChargerRegistered(String chargerName) {
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordFromName(chargerName);
        return !Objects.isNull(chargerRecord);
    }
}

