package com.blucharge.ocpp.dto;

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
