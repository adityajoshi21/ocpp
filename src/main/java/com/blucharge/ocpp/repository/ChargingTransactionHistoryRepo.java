package com.blucharge.ocpp.repository;

import com.blucharge.db.ocpp.tables.records.ChargingTransactionHistoryRecord;
import com.blucharge.ocpp.enums.Reason;
import org.joda.time.DateTime;

public interface ChargingTransactionHistoryRepo {
    ChargingTransactionHistoryRecord createRecord(ChargingTransactionHistoryRecord chargingTransactionHistoryRecord);

    void updateTransactionForStopTransaction(Integer transactionId, Integer meterStopValue, DateTime stopTime, Reason reason, Long startTime, Integer meterStartValue, Integer stopSoc);

    ChargingTransactionHistoryRecord getChargingTxnHistoryRecordForUuid(String txnId);

    ChargingTransactionHistoryRecord getChargingTxnHistoryRecordForId(long txnId);

    void updateTransactionForStartSoc(Long id, String socMeasurand);
}
