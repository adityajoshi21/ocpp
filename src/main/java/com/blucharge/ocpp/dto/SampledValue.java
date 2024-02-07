package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampledValue {
    private String value;
    private ReadingContext context;
    private ValueFormat format;
    private Measurand measurand;
    private Phase phase;
    private Location location;
    private UnitOfMeasure unit;
}