package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.TransactionSummary;
import com.blucharge.db.ocpp.tables.records.TransactionSummaryRecord;
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
    private static final TransactionSummary transactionHistory = TransactionSummary.TRANSACTION_SUMMARY;
    @Override
    public TransactionSummaryRecord doesTransactionExistsInTransactionHistory(Long TransactionId) {
        TransactionSummaryRecord transactionHistoryRecord = dslContext.select()
                .from(transactionHistory)
                .where(transactionHistory.TRANSACTION_ID.eq(TransactionId))
                .fetchOneInto(TransactionSummaryRecord.class);
        return  transactionHistoryRecord;
    }

    @Override
    public void addTransactionInTransactionHistory(Long TransactionId, String idTag, Long chargerId, Integer connectorNo, BigDecimal startValue, String startOn) {
        TransactionSummaryRecord transactionSummaryRecord = dslContext.newRecord(transactionHistory);
        transactionSummaryRecord.setTransactionId(TransactionId);
        transactionSummaryRecord.setIdTag(idTag);
        transactionSummaryRecord.setChargerId(chargerId);
        transactionSummaryRecord.setConnectorNumber(connectorNo);
        transactionSummaryRecord.setMeterStartValue(startValue);
        transactionSummaryRecord.setStopOn(DateTime.parse(startOn));
        transactionSummaryRecord.setIsActive(true);
        transactionSummaryRecord.store();
    }

}
