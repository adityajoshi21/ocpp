package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargingRateUnitType {
    W(""),
    A("A");
    private final String value;

    ChargingRateUnitType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChargingRateUnitType fromValue(String value) {
        for (ChargingRateUnitType enumValue : ChargingRateUnitType.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ChargingRateUnitType value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
