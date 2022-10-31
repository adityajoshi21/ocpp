package com.blucharge.ocpp.repository.impl;

import com.blucharge.ocpp.repository.TransactionsRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionsRepositoryImpl implements TransactionsRepository {

    @Autowired
    private DSLContext dslContext;

}
