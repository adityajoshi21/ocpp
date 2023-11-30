package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;

public interface LiveTransactionRepo {
    void createRecord(LiveTransactionRecord liveTransactionRecord);

    void updateTransactionForMeterValue(Long transactionId, String socMeasurand, String energyImported, Boolean value);

    LiveTransactionRecord getLiveTransactionRecordForTxnId(long txnId);

    void deleteRecord(Long liveTxnId);
}
