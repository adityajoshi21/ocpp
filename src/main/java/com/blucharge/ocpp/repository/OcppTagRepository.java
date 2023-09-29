package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.OcppTagRecord;

public interface OcppTagRepository {
    OcppTagRecord  getRecord(String idTag);
    OcppTagRecord getRecord(Long ocppTagPk);
 //   boolean insertIgnoreIdTag(StartTransactionRequest request);



}
