package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Charger;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.repository.ChargerRepo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ChargerRepoImpl implements ChargerRepo {
    private static final Charger charger = Charger.CHARGER;
    @Autowired
    private final DSLContext ctx;

    public ChargerRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void updateBootNotificationForCharger(BootNotificationRequest request, String chargerName) {
        ctx.update(charger)
                .set(charger.CHARGE_POINT_VENDOR, request.getChargePointVendor())
                .set(charger.CHARGE_POINT_MODEL, request.getChargePointModel())
                .set(charger.CHARGE_POINT_SERIAL_NUMBER, request.getChargePointSerialNumber())
                .set(charger.CHARGE_BOX_SERIAL_NUMBER, request.getChargeBoxSerialNumber())
                .set(charger.FIRMWARE_VERSION, request.getFirmwareVersion())
                .set(charger.ICCID, request.getIccid())
                .set(charger.IMSI, request.getImsi())
                .set(charger.METER_TYPE, request.getMeterType())
                .set(charger.METER_SERIAL_NUMBER, request.getMeterSerialNumber())
                .set(charger.LAST_HEARTBEAT_ON, DateTime.now())
                .where(charger.NAME.equal(chargerName))
                .and(charger.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public void updateChargerHeartBeat(Long chargerId, DateTime dateTime) {
        ctx.update(charger)
                .set(charger.LAST_HEARTBEAT_ON, dateTime)
                .set(charger.UPDATED_ON, dateTime)
                .where(charger.ID.equal(chargerId)
                        .and(charger.IS_ACTIVE.eq(true)))
                .execute();
    }

    @Override
    public ChargerRecord getChargerRecordFromName(String chargerName) {
        return ctx.selectFrom(charger)
                .where(charger.IS_ACTIVE.eq(true))
                .and(charger.NAME.eq(chargerName))
                .fetchOneInto(ChargerRecord.class);
    }

    @Override
    public ChargerRecord getChargerRecordForId(Long chargerId) {
        return ctx.selectFrom(charger)
                .where(charger.IS_ACTIVE.eq(true))
                .and(charger.ID.eq(chargerId))
                .fetchOneInto(ChargerRecord.class);
    }
}
