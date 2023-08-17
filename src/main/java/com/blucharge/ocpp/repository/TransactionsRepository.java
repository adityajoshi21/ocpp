package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.InsertTransactionParams;
import com.blucharge.ocpp.dto.ws.UpdateTransactionParams;

import java.math.BigDecimal;

public interface TransactionsRepository {
    TransactionRecord getTransactionForParams(Long chargerId, String idTag, Integer connectorId, BigDecimal meterStartVal);

    Long insertTransaction(InsertTransactionParams params, Long chargerId);
    Transaction updateTransaction(UpdateTransactionParams params);
    TransactionRecord getActiveTransctionForTxnId(Long transactionId);
    Long addTransaction(TransactionRecord record);

//    Transaction getDetails(Long transactionPk);


}
