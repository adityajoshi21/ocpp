package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.analytics.tables.HubwiseChargerUptime;
import com.blucharge.db.analytics.tables.records.HubwiseChargerUptimeRecord;
import com.blucharge.ocpp.repository.HubWiseUpTimeRepo;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HubWiseUpTimeRepoImpl implements HubWiseUpTimeRepo {
    private static final HubwiseChargerUptime hubwiseChargerUptime = HubwiseChargerUptime.HUBWISE_CHARGER_UPTIME;
    private static final com.blucharge.db.ocpp.tables.HubwiseChargerUptime hubwiseChargerUptime1 = com.blucharge.db.ocpp.tables.HubwiseChargerUptime.HUBWISE_CHARGER_UPTIME;

    @Override
    public void createAnalyticsHubUpTimeRecord(com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord hubwiseChargerUptimeRecord, DSLContext dslAnalyticsContext) {
        HubwiseChargerUptimeRecord hubwiseChargerUptimeRecord1 = dslAnalyticsContext.newRecord(hubwiseChargerUptime, hubwiseChargerUptimeRecord);
        hubwiseChargerUptimeRecord1.store();

    }

    @Override
    public List<com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord> getRecords(DSLContext dslOcppContext) {
        return dslOcppContext
                .selectFrom(hubwiseChargerUptime1)
                .fetchInto(com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord.class);

    }
}
