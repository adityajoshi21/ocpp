package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.ocpp.dto.ws.StartTransactionRequest;

public interface OcppTagRepository {
    OcppTagRecord  getRecord(String idTag);
    OcppTagRecord getRecord(Long ocppTagPk);
 //   boolean insertIgnoreIdTag(StartTransactionRequest request);



}
