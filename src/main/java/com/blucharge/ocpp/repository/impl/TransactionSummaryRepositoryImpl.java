package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.TransactionSummary;
import com.blucharge.db.ocpp.tables.records.TransactionSummaryRecord;
import com.blucharge.ocpp.repository.TransactionSummaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Repository
public class TransactionSummaryRepositoryImpl implements TransactionSummaryRepository {
   @Autowired
    private final DSLContext dslContext;

    public TransactionSummaryRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private static final TransactionSummary transactionHistory = TransactionSummary.TRANSACTION_SUMMARY;
    @Override
    public TransactionSummaryRecord doesTransactionExistsInTransactionHistory(Long transactionId) {
        TransactionSummaryRecord transactionHistoryRecord = dslContext.select()
                .from(transactionHistory)
                .where(transactionHistory.TRANSACTION_ID.eq(transactionId))
                .fetchOneInto(TransactionSummaryRecord.class);
        return  transactionHistoryRecord;
    }

    @Override
    public void addTransactionInTransactionSummary(Long TransactionId, String idTag, Long chargerId, Integer connectorNo) {
        TransactionSummaryRecord transactionSummaryRecord = dslContext.newRecord(transactionHistory);
        transactionSummaryRecord.setTransactionId(TransactionId);
        transactionSummaryRecord.setIdTag(idTag);
        transactionSummaryRecord.setChargerId(chargerId);
        transactionSummaryRecord.setConnectorNumber(connectorNo);
        transactionSummaryRecord.setIsActive(true);
        transactionSummaryRecord.store();
    }


    @Override
    public void updateTransactionInTransactionSummary(Long transactionId, BigDecimal unitsConsumed, Duration duration, BigDecimal socGain, String stopReason) {
        TransactionSummaryRecord transactionSummaryRecord = dslContext.selectFrom(transactionHistory).where(transactionHistory.TRANSACTION_ID.eq(transactionId)).and(transactionHistory.IS_ACTIVE.eq(true)).fetchOneInto(TransactionSummaryRecord.class);
        if(!Objects.isNull(transactionSummaryRecord)){
            transactionSummaryRecord.setUnitsConsumed(unitsConsumed);
            transactionSummaryRecord.setSocGained(socGain);
//            transactionSummaryRecord.setRunningTime();
            transactionSummaryRecord.setStopReason(stopReason);
            transactionSummaryRecord.update();
        }
        log.info("Transaction with Transaction ID {} not found in Txn Summary", transactionId);
    }

}
