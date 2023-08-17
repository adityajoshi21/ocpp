package com.blucharge.ocpp.service.impl;

import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.constants.OcppConstants;
import com.blucharge.ocpp.dto.ChargerRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationResponse;
import com.blucharge.ocpp.dto.ws.HeartbeatRequest;
import com.blucharge.ocpp.dto.ws.HeartbeatResponse;
import com.blucharge.ocpp.enums.OcppProtocol;
import com.blucharge.ocpp.enums.OcppVersion;
import com.blucharge.ocpp.enums.RegistrationStatus;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.service.ChargerService;
import com.google.common.util.concurrent.Striped;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.locks.Lock;

import static com.blucharge.ocpp.constants.ApplicationConstants.HEARTBEAT_INTERVAL;


@Slf4j
@Service
public class ChargerServiceImpl implements ChargerService {

    @Autowired
    private ChargerRepository chargerRepository;
   // @Autowired private ChargerHelperService chargePointHelperService;


    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);
    @Override
    public BootNotificationResponse bootNotification(BootNotificationRequest parameters,  OcppProtocol protocol, String chargerIdentity) {
        if (protocol.getVersion() != OcppVersion.V_16) {
            throw new IllegalArgumentException("Unexpected OCPP version: " + protocol.getVersion() + "We only support 1.6");
        }

        boolean isRegistered = isRegistered(chargerIdentity);         //check if charger exists in DB
        DateTime now = DateTime.now();

        if (isRegistered) {
            log.info("The charger '{}' is registered and its boot notification is acknowledged.", chargerIdentity);
            chargerRepository.updateBootNotificationForCharger(parameters, protocol, chargerIdentity);
        } else {
            log.error("The charger '{}' is NOT registered and its boot is NOT acknowledged.", ApplicationConstants.TEST_CHARGER);
            //chargePointHelperService.rememberNewUnknowns(chargerIdentity);       //To clarify in review
        }

        return new BootNotificationResponse()
                .withStatus(isRegistered ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED)
                .withCurrentTime(now)
                .withInterval(HEARTBEAT_INTERVAL);
    }

    @Override
    public HeartbeatResponse heartbeat(HeartbeatRequest request, String chargerIdentity) {
        DateTime now = DateTime.now();
        chargerRepository.updateChargerHeartbeat(chargerIdentity, now);
        return new HeartbeatResponse().withCurrentTime(now);
    }

    @Override
    public boolean isRegistered(String chargerName) {
        Lock l = isRegisteredLocks.get(chargerName);
        l.lock();

        try {
            if (chargerRepository.isRegisteredInternal(chargerName)) {
                return true;
            }
            try {
                ChargerRequest request = new ChargerRequest();
                request.setChargerName(chargerName);
                request.setUuid(UUID.randomUUID().toString());
                Integer noOfConnectors = 0;       //Need to find out how to fetch this value
                request.setNoOfConnectors(noOfConnectors);

                Long insertedChargerId = chargerRepository.addCharger(request);

                log.warn("Auto-registered unknown charger '{}' with chargerId '{}'", chargerName, insertedChargerId);
                return true;
            } catch (Exception e) {
                log.error("Failed to auto-register unknown charger '" + chargerName + "'", e);
                return false;
            }
        } finally {
            l.unlock();
        }
    }

}
