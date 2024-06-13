package com.blucharge.ocpp.repository;

import org.jooq.DSLContext;

import java.util.List;


public interface HubWiseUpTimeRepo {

    void createAnalyticsHubUpTimeRecord(com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord hubwiseChargerUptimeRecord, DSLContext dslAnalyticsContext);

    List<com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord> getRecords(DSLContext dslOcppContext);
}
