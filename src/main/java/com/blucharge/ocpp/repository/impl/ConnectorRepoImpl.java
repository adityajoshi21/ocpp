package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Connector;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.status_notification.StatusNotificationRequest;
import com.blucharge.ocpp.repository.ConnectorRepo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
public class ConnectorRepoImpl implements ConnectorRepo {
    private static final Connector connector = Connector.CONNECTOR;
    @Autowired
    private final DSLContext ctx;

    public ConnectorRepoImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void updateConnectorStatus(StatusNotificationRequest params, Long connectorId) {
        ctx.update(connector)
                .set(connector.STATUS_NOTIFICATION_ON, params.getTimestamp())
                .set(connector.STATUS, params.getStatus().name())
                .set(connector.ERROR_CODE, params.getErrorCode().name())
                .set(connector.ERROR_INFO, params.getInfo())
                .set(connector.VENDOR_ERROR_CODE, params.getVendorErrorCode())
                .where(connector.ID.eq(connectorId))
                .and(connector.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public ConnectorRecord getConnectorRecordForChargerIdAndConnectorNumber(Long chargerId, Integer connectorNumber) {
        return ctx.selectFrom(connector)
                .where(connector.NUMBER.eq(connectorNumber))
                .and(connector.CHARGER_ID.eq(chargerId))
                .and(connector.IS_ACTIVE.eq(true))
                .fetchOneInto(ConnectorRecord.class);
    }


    @Override
    public List<ConnectorRecord> getConnectorRecordForChargerId(Long chargerId) {
        return ctx.selectFrom(connector)
                .where(connector.CHARGER_ID.eq(chargerId))
                .and(connector.IS_ACTIVE.eq(true))
                .fetchInto(ConnectorRecord.class);
    }

    @Override
    public void updateConnectorHeartBeat(Long connectorId, DateTime dateTime) {
        ctx.update(connector)
                .set(connector.LAST_HEARTBEAT_ON, dateTime)
                .set(connector.UPDATED_ON, dateTime)
                .where(connector.ID.eq(connectorId))
                .and(connector.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public ConnectorRecord getConnectorRecordFromUuid(String connectorId) {
        return ctx.selectFrom(connector)
                .where(connector.UUID.eq(connectorId))
                .and(connector.IS_ACTIVE.eq(true))
                .fetchOneInto(ConnectorRecord.class);
    }
}
