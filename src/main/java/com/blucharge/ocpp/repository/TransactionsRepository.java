package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;

public interface TransactionsRepository {
    void updateTransaction(UpdateTransactionParams params);
    TransactionRecord getActiveTransactionOnConnectorNoForTxnId(Long transactionId,Integer connectorNo);
    Long addTransaction(TransactionRecord record);
    Integer findConnectorNoForTransactionId(Long transactionId);
    TransactionRecord getActiveTransactionOnConnectorId(Integer connectorId, Long chargerId);

    TransactionRecord getInactiveTransactionOnConnectorId(Long txnId, Integer connectorId);

    void stopChargingInitiatedFromRemoteStart(TransactionRecord txnRecord);

    //TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal);
}
