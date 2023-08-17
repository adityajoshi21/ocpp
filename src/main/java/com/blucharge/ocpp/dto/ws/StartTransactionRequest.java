package com.blucharge.ocpp.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartTransactionRequest {
    @NotNull
    private Integer connectorId;
    @NotNull
    private String idTag;
    @NotNull
    private BigDecimal meterStartValue;
    private DateTime timestamp;

    // To implement reservation id in v2

}
