package com.blucharge.ocpp.dto.start_transaction;

import com.blucharge.ocpp.dto.IdToken;
import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StartTransactionRequest {
    private Integer connectorId;
    private IdToken idTag;
    private Integer meterStartValue;
    private Integer reservationId;
    private DateTime timestamp;
}
