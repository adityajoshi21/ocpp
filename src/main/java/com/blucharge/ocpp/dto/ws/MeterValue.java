package com.blucharge.ocpp.dto.ws;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MeterValue {
    private DateTime timestamp;
    private List<SampledValue> sampledValue;

}
