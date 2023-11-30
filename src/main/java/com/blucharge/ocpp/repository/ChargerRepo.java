package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import org.joda.time.DateTime;

public interface ChargerRepo {
    void updateBootNotificationForCharger(BootNotificationRequest parameters, String chargerName);

    void updateChargerHeartBeat(Long chargerId, DateTime dateTime);

    ChargerRecord getChargerRecordFromName(String chargerName);

    ChargerRecord getChargerRecordForId(Long chargerId);
}
