package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Charger;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.ChargerRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.repository.ChargerRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ChargerRepositoryImpl implements ChargerRepository {
    private static final Charger charger = Charger.CHARGER;
    @Autowired
    private final DSLContext ctx;

    public ChargerRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    @Override
    public Long addCharger(ChargerRequest chargerRequest) {
        ChargerRecord record = ctx.newRecord(charger, chargerRequest);
        record.setIsActive(true);
        record.store();
        return record.getId();
    }

    @Override
    public void updateBootNotificationForCharger(BootNotificationRequest request,  String chargerName) {
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
    public List<ChargerRecord> getChargerFromChargerId(String chargerName) {
        return ctx.selectFrom(charger)
                .where(charger.CHARGER_NAME.equal(chargerName))
                .and(charger.IS_ACTIVE.eq(true))
                .fetchInto(ChargerRecord.class);
    }

    @Override
    public void updateNumberOfConnectors(Long chargerId, Integer currentCount) {
        ctx.update(charger)
                .set(charger.NO_OF_CONNECTORS, currentCount)
                .where(charger.ID.eq(chargerId)).and(charger.IS_ACTIVE.eq(true)).execute();
    }

    @Override
    public Integer findNoOfConnectorsForCharger(Long chargerId) {
        return ctx.select(charger.NO_OF_CONNECTORS)
                .from(charger)
                .where(charger.ID.eq(chargerId))
                .and(charger.IS_ACTIVE.eq(true))
                .fetchOneInto(Integer.class);
    }

    @Override
    public ChargerRecord getChargerRecordFromName(String chargerName) {
        return ctx.selectFrom(charger)
                .where(charger.IS_ACTIVE.eq(true))
                .and(charger.NAME.eq(chargerName))
                .fetchOneInto(ChargerRecord.class);
    }
}
