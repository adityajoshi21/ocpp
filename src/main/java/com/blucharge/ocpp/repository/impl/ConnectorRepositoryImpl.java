package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.Connector;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.enums.ConnectorStatus;
import com.blucharge.ocpp.repository.ConnectorRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



@Slf4j
@Repository
public class ConnectorRepositoryImpl implements ConnectorRepository {
    @Autowired
    private final DSLContext ctx;

    public ConnectorRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    private static final Connector connector = Connector.CONNECTOR;
    @Override
    public Long getConnectorPkForChargeBoxAndConnectorNumber(Long chargerId, Integer connectorNumber) {
        return ctx.selectFrom(connector)
                .where(connector.CHARGER_ID.eq(chargerId))
                .and(connector.CONNECTOR_NUMBER.eq(connectorNumber))
                .fetchAny(connector.ID);
    }

    @Override
    public void updateConnectorStatus(StatusNotificationRequest params, Long chargerId) {

        ctx.update(connector).set(connector.STATUS_NOTIFICATION_ON, params.getTimestamp())
                .set(connector.STATUS, params.getStatus())
                .set(connector.ERROR_CODE, params.getErrorCode())
                .set(connector.ERROR_INFO, params.getErrorInfo())
                .set(connector.VENDOR_ERROR_CODE, params.getVendorErrorCode())
                .where(connector.CONNECTOR_NUMBER.eq(params.getConnectorId())).and(connector.CHARGER_ID.eq(chargerId))
                .execute();
        log.debug("Stored a new connector status for chargerId: {}/ connector: {}.", chargerId, params.getConnectorId());
    }


    @Override
    public void updateConnectorState(Long transactionId, Long connectorPk, ConnectorState state) {
        ctx.update(connector)
                .set(connector.STATE, state.name())
                .where(connector.ID.eq(connectorPk))
                .and(connector.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public ConnectorRecord getConnectorForChargerIdWithConnectorNumber(Long chargerId, Integer connectorNumber) {
        return ctx.selectFrom(connector)
                .where(connector.CONNECTOR_NUMBER.eq(connectorNumber))
                .and(connector.CHARGER_ID.eq(chargerId))
                .and(connector.IS_ACTIVE.eq(true))
                .fetchOneInto(ConnectorRecord.class);
    }

    @Override
    public Long addConnector(ConnectorRecord request) {        //To Do : If needs to be moved to service?

        ConnectorRecord connectorRecord = ctx.newRecord(connector, request);
        connectorRecord.setIsActive(true);
        connectorRecord.store();
        return connectorRecord.getId();
    }

    @Override
    public ConnectorRecord getConnectorRecordFromConnectorId(Long connectorId, Long chargerId) {
        return ctx.selectFrom(connector)
                .where(connector.ID.eq(connectorId).
                        and(connector.CHARGER_ID.eq(chargerId))
                        .and(connector.IS_ACTIVE.eq(true)))
                .fetchOneInto(ConnectorRecord.class);
    }


    @Override
    public void updateConnectorStatus(
            Long connectorPk,
            DateTime timestamp,
            ConnectorStatus statusUpdate) {

        ctx.update(connector)
                .set(connector.STATUS_NOTIFICATION_ON, timestamp)
                .set(connector.STATUS, statusUpdate.name())
                .where(connector.ID.eq( connectorPk))
                .execute();
    }

}
