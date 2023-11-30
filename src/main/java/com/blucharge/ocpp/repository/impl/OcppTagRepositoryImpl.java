package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.OcppTag;
import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.ocpp.repository.OcppTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OcppTagRepositoryImpl implements OcppTagRepository {
    private static final OcppTag ocppTag = OcppTag.OCPP_TAG;
    @Autowired
    private final DSLContext ctx;

    public OcppTagRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public OcppTagRecord getOcppTagRecordForIdTag(String idTag) {
        return ctx.selectFrom(ocppTag)
                .where(ocppTag.ID_TAG.equal(idTag))
                .and(ocppTag.IS_ACTIVE.equal(true))
                .fetchOneInto(OcppTagRecord.class);
    }

    @Override
    public OcppTagRecord getOcppTagForCustomer(String customerId) {
        return ctx.selectFrom(ocppTag)
                .where(ocppTag.UUID.equal(customerId))
                .and(ocppTag.IS_ACTIVE.equal(true))
                .fetchOneInto(OcppTagRecord.class);
    }
}
