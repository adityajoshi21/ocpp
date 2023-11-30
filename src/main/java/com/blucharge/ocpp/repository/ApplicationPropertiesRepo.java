package com.blucharge.ocpp.repository;


import com.blucharge.db.ocpp.tables.records.ApplicationPropertiesRecord;

import java.util.Map;

public interface ApplicationPropertiesRepo {
    Map<String, ApplicationPropertiesRecord> getPropertyMap();
}
