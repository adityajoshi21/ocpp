package com.blucharge.ocpp.dto.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Integer connectorId;
    private String idTag;
    private BigDecimal meterStartValue;
    private DateTime timestamp;

    // To implement reservation id in v2

}
