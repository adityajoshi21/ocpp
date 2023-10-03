package com.blucharge.ocpp.repository;
import com.blucharge.db.ocpp.tables.records.TransactionSummaryRecord;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface TransactionHistoryRepository {

    TransactionSummaryRecord doesTransactionExistsInTransactionHistory(Long TransactionId);
    void addTransactionInTransactionHistory(Long TransactionId, String idTag, Long chargerId, Integer connectorNo, BigDecimal startValue, String startOn);
}
