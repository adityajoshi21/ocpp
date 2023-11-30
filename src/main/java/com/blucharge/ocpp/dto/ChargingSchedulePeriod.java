package com.blucharge.ocpp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargingSchedulePeriod {
    private Integer startPeriod;
    private double limit;
    private Integer numberPhases;
}
