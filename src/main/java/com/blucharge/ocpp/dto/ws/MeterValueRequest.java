package com.blucharge.ocpp.dto.ws;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MeterValueRequest{
    private Integer connectorId;
    private Long transactionId;
    private List<MeterValue> meterValue;

    public boolean isSetMeterValue() {
        return ((this.meterValue!= null)&&(!this.meterValue.isEmpty()));
    }
}
