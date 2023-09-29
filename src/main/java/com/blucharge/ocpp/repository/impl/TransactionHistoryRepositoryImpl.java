package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.TransactionHistory;
import com.blucharge.db.ocpp.tables.records.TransactionHistoryRecord;
import com.blucharge.ocpp.repository.TransactionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Slf4j
@Repository
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepository {
   @Autowired
    private final DSLContext dslContext;

    public  TransactionHistoryRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private static final TransactionHistory transactionHistory = TransactionHistory.TRANSACTION_HISTORY;
    @Override
    public TransactionHistoryRecord doesTransactionExistsInTransactionHistory(Long TransactionId) {
        TransactionHistoryRecord transactionHistoryRecord = dslContext.select()
                .from(transactionHistory)
                .where(transactionHistory.TRANSACTION_ID.eq(TransactionId))
                .fetchOneInto(TransactionHistoryRecord.class);
        return  transactionHistoryRecord;
    }

    @Override
    public void addTransactionInTransactionHistory(Long TransactionId, String idTag, Long chargerId, Integer connectorNo, BigDecimal startValue, String startOn) {
        TransactionHistoryRecord transactionHistoryRecord = dslContext.newRecord(transactionHistory);
        transactionHistoryRecord.setTransactionId(TransactionId);
        transactionHistoryRecord.setIdTag(idTag);
        transactionHistoryRecord.setChargerId(chargerId);
        transactionHistoryRecord.setConnectorNumber(connectorNo);
        transactionHistoryRecord.setStartValue(startValue);
        transactionHistoryRecord.setStartTime(startOn);
        transactionHistoryRecord.setIsActive(true);
        transactionHistoryRecord.store();
    }

}
