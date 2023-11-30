package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.api.UnlockConnectorRequest;
import com.blucharge.ocpp.dto.api.UnlockConnectorResponse;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationResponse;
import com.blucharge.ocpp.enums.RegistrationStatus;
import com.blucharge.ocpp.repository.ChargerRepo;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.service.ConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private ChargerRepo chargerRepo;

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
        connectorRepository.updateConnectorStatus(parameters, chargerRecord.getId());
        // todo add kafka event for connector status
        return new StatusNotificationResponse(RegistrationStatus.ACCEPTED.name());
    }


    @Override
    public UnlockConnectorResponse unlockConnector(UnlockConnectorRequest request, String chargerName) {
        return new UnlockConnectorResponse();
    }
}