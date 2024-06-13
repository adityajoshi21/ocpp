package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.ApplicationProperties;
import com.blucharge.db.ocpp.tables.records.ApplicationPropertiesRecord;
import com.blucharge.ocpp.repository.ApplicationPropertiesRepo;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Slf4j
@Repository
public class ApplicationPropertiesRepoImpl implements ApplicationPropertiesRepo {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.APPLICATION_PROPERTIES;

    @Override
    public Map<String, ApplicationPropertiesRecord> getPropertyMap(DSLContext ctx) {
        return ctx.select().from(applicationProperties)
                .where(applicationProperties.IS_ACTIVE.eq(true))
                .fetchMap(applicationProperties.PROPERTY_NAME, ApplicationPropertiesRecord.class);
    }
}
