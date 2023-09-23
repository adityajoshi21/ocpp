package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;

public interface TransactionsRepository {
    void updateTransaction(UpdateTransactionParams params);
    TransactionRecord getActiveTransctionForTxnId(Long transactionId);
    Long addTransaction(TransactionRecord record);
    Long findConnectorIdForTransactionId(Long transactionId);
    TransactionRecord getActiveTransactionOnConnectorId(Long connectorId);
    void stopChargingScreen(TransactionRecord txnRecord);
    Boolean isTransactionRunningOnConnectorId(Long connectorPk);

    //TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal);
}
