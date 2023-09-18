package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface TransactionsRepository {
    void updateTransaction(UpdateTransactionParams params);
    TransactionRecord getActiveTransctionForTxnId(Long transactionId);
    Long addTransaction(TransactionRecord record);
    Long findConnectorPkForTransactionId(Long transactionId);
    TransactionRecord getActiveTransactionOnConnectorId(Long connectorId);
    void stopChargingScreen(TransactionRecord txnRecord);
    Boolean isTransactionRunningOnConenctorId(Long connectorPk);

    //TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal);
}
