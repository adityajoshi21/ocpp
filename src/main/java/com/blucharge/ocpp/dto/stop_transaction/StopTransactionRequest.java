package com.blucharge.ocpp.dto.stop_transaction;

import com.blucharge.ocpp.dto.IdToken;
import com.blucharge.ocpp.dto.MeterValue;
import com.blucharge.ocpp.enums.Reason;
import lombok.*;
import org.joda.time.DateTime;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StopTransactionRequest {
    protected List<MeterValue> transactionData;
    private IdToken idTag;
    private Integer meterStop;
    private DateTime timestamp;
    private Integer transactionId;
    private Reason reason;
}