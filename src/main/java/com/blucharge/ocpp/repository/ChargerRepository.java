package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.ChargerRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.enums.OcppProtocol;
import org.joda.time.DateTime;

public interface ChargerRepository {
    Long addCharger(ChargerRequest chargerRequest);

    boolean isRegisteredInternal(String chargerId);

    void updateBootNotificationForCharger(BootNotificationRequest parameters, OcppProtocol protocol, String chargerIdentity);
    void updateChargerHeartbeat(String chargeBoxIdentity, DateTime ts);

    ChargerRecord getChargerFromChargerId(String chargerIdentity);

   void updateNumberOfConnectors (Long chargerId, Integer currentCount);

}
