package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.ChargingRateUnitType;
import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargingSchedule {
    private Integer duration;
    private DateTime startSchedule;
    private ChargingRateUnitType chargingRateUnit;
    private ChargingSchedulePeriod chargingSchedulePeriod;
    private double minChargingRate;
}
