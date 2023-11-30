package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.records.ApplicationPropertiesRecord;
import com.blucharge.ocpp.repository.ApplicationPropertiesRepo;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Slf4j
@Repository
public class ApplicationPropertiesRepoImpl implements ApplicationPropertiesRepo {
    private static final com.blucharge.db.ocpp.tables.ApplicationProperties applicationProperties = com.blucharge.db.ocpp.tables.ApplicationProperties.APPLICATION_PROPERTIES;
    @Autowired
    private DSLContext ctx;

    @Override
    public Map<String, ApplicationPropertiesRecord> getPropertyMap() {
        return ctx.select().from(applicationProperties)
                .where(applicationProperties.IS_ACTIVE.eq(true))
                .fetchMap(applicationProperties.PROPERTY_NAME, ApplicationPropertiesRecord.class);
    }
}
