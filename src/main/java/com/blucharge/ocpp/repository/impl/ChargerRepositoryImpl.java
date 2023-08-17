package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Charger;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.ocpp.dto.ChargerRequest;
import com.blucharge.ocpp.dto.ws.BootNotificationRequest;
import com.blucharge.ocpp.enums.OcppProtocol;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.google.common.util.concurrent.Striped;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.concurrent.locks.Lock;




@Repository
@Slf4j
public class ChargerRepositoryImpl implements ChargerRepository {
    private static final Charger charger = Charger.CHARGER;

    private final DSLContext ctx;


    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    public ChargerRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    public boolean isRegisteredInternal(String chargerId) {
        Integer count = ctx.selectCount()
                .from(charger)
                .where(charger.CHARGER_NAME.eq(chargerId))
                .and(charger.IS_ACTIVE.eq(true))
                .fetchOneInto(Integer.class);
        return count.equals(1);
    }

    @Override
    public Long addCharger(ChargerRequest request) {        //To Do : If needs to be moved to service?

        ChargerRecord record = ctx.newRecord(charger, request);
        record.setIsActive(true);
        record.store();
        return record.getId();
    }

    @Override
    public void updateCharger(BootNotificationRequest p, OcppProtocol protocol, String chargerIdentity) {
        ctx.update(charger)
                .set(charger.OCPP_VERSION, protocol.getCompositeValue())
                .set(charger.CHARGE_POINT_VENDOR, p.getChargePointVendor())
                .set(charger.CHARGE_POINT_MODEL, p.getChargePointModel())
                .set(charger.CHARGE_POINT_SERIAL_NUMBER, p.getChargePointSerialNumber())
                .set(charger.CHARGE_BOX_SERIAL_NUMBER, p.getChargeBoxSerialNumber())
                .set(charger.FIRMWARE_VERSION, p.getFirmwareVersion())
                .set(charger.ICCID, p.getIccid())
                .set(charger.IMSI, p.getImsi())
                .set(charger.METER_TYPE, p.getMeterType())
                .set(charger.METER_SERIAL_NUMBER, p.getMeterSerialNumber())
                .set(charger.LAST_HEARTBEAT_ON, DateTime.now())
                .where(charger.CHARGER_NAME.equal(chargerIdentity)).and(charger.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public void updateChargerHeartbeat(String chargerIdentity, DateTime ts) {
        ctx.update(charger)
                .set(charger.LAST_HEARTBEAT_ON, ts)
                .set(charger.UPDATED_ON, ts)
                .where(charger.CHARGER_NAME.equal(chargerIdentity).and(charger.IS_ACTIVE.eq(true)))
                .execute();
    }

    @Override
    public ChargerRecord getChargerFromChargerId(String chargerIdentity) {
        return ctx.selectFrom(charger)
                .where(charger.CHARGER_NAME.equal(chargerIdentity))
                .and(charger.IS_ACTIVE.eq(true))
                .fetchOneInto(ChargerRecord.class);
    }

    @Override
    public void updateNumberOfConnectors(Long chargerId, Integer currentCount) {
        ctx.update(charger)
                .set(charger.NO_OF_CONNECTORS, currentCount)
                .where(charger.ID.eq(chargerId)).and(charger.IS_ACTIVE.eq(true)).execute();
    }
}
