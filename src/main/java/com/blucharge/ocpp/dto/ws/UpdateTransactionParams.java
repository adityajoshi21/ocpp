package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Data
@Builder
public class UpdateTransactionParams {
    private Long chargerId;
    private final Long transactionId;
    private final DateTime stopTimestamp;
    private final BigDecimal stopMeterValue;
    private final String stopReason;

    private  final TransactionStatusUpdate statusUpdate = TransactionStatusUpdate.AfterStop;
}