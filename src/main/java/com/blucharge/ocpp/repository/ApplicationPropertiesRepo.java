package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ApplicationPropertiesRecord;
import org.jooq.DSLContext;

import java.util.Map;

public interface ApplicationPropertiesRepo {
    Map<String, ApplicationPropertiesRecord> getPropertyMap(DSLContext context);
}
