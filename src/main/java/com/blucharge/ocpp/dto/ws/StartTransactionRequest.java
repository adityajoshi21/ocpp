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

    @NotNull (message = "Connector ID not found in request")
    private Integer connectorId;
    @NotNull (message = "IdTag not sent in Start Txn request")
    private String idTag;
    @NotNull (message = "Start Meter value not found in request")
    private BigDecimal meterStartValue;
    private DateTime timestamp;

    // To implement reservation id in v2

}
