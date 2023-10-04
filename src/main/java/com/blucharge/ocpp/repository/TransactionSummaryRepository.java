package com.blucharge.ocpp.repository;
import com.blucharge.db.ocpp.tables.records.TransactionSummaryRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.math.BigDecimal;

public interface TransactionSummaryRepository {

    TransactionSummaryRecord doesTransactionExistsInTransactionHistory(Long TransactionId);
    void addTransactionInTransactionSummary(Long transactionId, String idTag, Long chargerId, Integer connectorNo);

    void updateTransactionInTransactionSummary(Long transactionId, BigDecimal unitsConsumed, Duration duration, BigDecimal socGain, String stopReason);
}
