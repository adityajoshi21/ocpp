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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.locks.Lock;

import static com.blucharge.ocpp.constants.ApplicationConstants.OCPP_VERSION;


@Repository
@Slf4j
public class ChargerRepositoryImpl implements ChargerRepository {
    private static final Charger charger = Charger.CHARGER;

    @Autowired
    private final DSLContext ctx;


    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    public ChargerRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    public Boolean isRegisteredInternal(String chargerId) {
        Integer count = ctx.selectCount()
                .from(charger)
                .where(charger.CHARGER_NAME.eq(chargerId))
                .and(charger.IS_ACTIVE.eq(true))
                .fetchOneInto(Integer.class);
        return count.equals(1);
    }

    @Override
    public Long addCharger(ChargerRequest chargerRequest) {
        ChargerRecord record = ctx.newRecord(charger, chargerRequest);
        record.setIsActive(true);
        record.store();
        return record.getId();
    }

    @Override
    public void updateBootNotificationForCharger(BootNotificationRequest request,  String chargerIdentity) {
        ctx.update(charger)
                .set(charger.CHARGE_POINT_VENDOR, request.getChargePointVendor())
                .set(charger.CHARGE_POINT_MODEL, request.getChargePointModel())
                .set(charger.END_POINT_URL, "")
                .set(charger.CHARGE_POINT_SERIAL_NUMBER, request.getChargePointSerialNumber())
                .set(charger.CHARGE_BOX_SERIAL_NUMBER, request.getChargeBoxSerialNumber())
                .set(charger.OCPP_VERSION, OCPP_VERSION)
                .set(charger.FIRMWARE_VERSION, request.getFirmwareVersion())
                .set(charger.ICCID, request.getIccid())
                .set(charger.IMSI, request.getImsi())
                .set(charger.METER_TYPE, request.getMeterType())
                .set(charger.METER_SERIAL_NUMBER, request.getMeterSerialNumber())
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
