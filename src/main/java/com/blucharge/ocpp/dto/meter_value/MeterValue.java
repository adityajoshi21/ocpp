package com.blucharge.ocpp.dto.meter_value;

import com.blucharge.ocpp.dto.SampledValue;
import lombok.*;
import org.joda.time.DateTime;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeterValue {
    private DateTime timestamp;
    private List<SampledValue> sampledValue;
}
