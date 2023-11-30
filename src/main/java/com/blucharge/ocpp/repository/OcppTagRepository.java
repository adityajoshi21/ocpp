package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.OcppTagRecord;

public interface OcppTagRepository {
    OcppTagRecord getOcppTagRecordForIdTag(String idToken);

    OcppTagRecord getOcppTagForCustomer(String customerId);
}
