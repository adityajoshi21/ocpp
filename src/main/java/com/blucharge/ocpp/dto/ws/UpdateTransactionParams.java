package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.dto.meter_value.MeterValue;
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
    private final Long transactionId;
    private final DateTime stopTimestamp;
    private final BigDecimal stopMeterValue;
    private final String stopReason;
    private final TransactionStateUpdate statusUpdate = TransactionStateUpdate.AfterStop;
    private Long chargerId;
    private List<MeterValue> transactionData;
}