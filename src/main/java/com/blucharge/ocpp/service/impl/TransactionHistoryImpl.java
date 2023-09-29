package com.blucharge.ocpp.service.impl;

import com.blucharge.ocpp.service.TransactionHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
@Slf4j
public class TransactionHistoryImpl implements TransactionHistory {
    @Override
    public Boolean insertTransactionInTransactionHistory(){
        return TRUE;
    }
}
