package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.TransactionStateUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpdateTransactionParams {
    private Long chargerId;
    private final Long transactionId;
    private final DateTime stopTimestamp;
    private final BigDecimal stopMeterValue;
    private final String stopReason;
    private List<MeterValue> transactionData;

    private  final TransactionStateUpdate statusUpdate = TransactionStateUpdate.AfterStop;
}