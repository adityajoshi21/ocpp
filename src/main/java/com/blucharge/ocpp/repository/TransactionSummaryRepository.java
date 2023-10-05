package com.blucharge.ocpp.repository;
import com.blucharge.db.ocpp.tables.records.TransactionSummaryRecord;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface TransactionSummaryRepository {

    TransactionSummaryRecord doesTransactionExistsInTransactionHistory(Long TransactionId);
    void addTransactionInTransactionSummary(Long transactionId, String idTag, Long chargerId, Integer connectorNo);

    void updateTransactionInTransactionSummary(Long transactionId, BigDecimal unitsConsumed, Long duration, BigDecimal socGain, String stopReason);
}
