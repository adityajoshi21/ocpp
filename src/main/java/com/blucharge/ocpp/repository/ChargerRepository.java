package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.ChargerRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import org.joda.time.DateTime;

public interface ChargerRepository {
    Long addCharger(ChargerRequest request);
    void updateBootNotificationForCharger(BootNotificationRequest parameters,  String chargerName);
    void updateChargerHeartBeat(Long chargerId, DateTime dateTime);
   void updateNumberOfConnectors (Long chargerId, Integer currentCount);
   Integer findNoOfConnectorsForCharger(Long chargerId);
    ChargerRecord getChargerRecordFromName(String chargerName);
}
