package com.blucharge.ocpp.dto.ws;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MeterValueRequest{
    @NotNull
    private Long connectorId;
    private Long transactionId;
    @NotNull
    private List<MeterValue> meterValue;

    public boolean isSetMeterValue() {
        return ((this.meterValue!= null)&&(!this.meterValue.isEmpty()));
    }
}
