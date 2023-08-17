package com.blucharge.ocpp.dto.ws;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

@Data
public class MeterValue {
    protected DateTime timestamp;
    protected List<SampledValue> sampledValue;


    public boolean isSetSampledValue() {
        return (this.sampledValue != null && !this.sampledValue.isEmpty());
    }
}
