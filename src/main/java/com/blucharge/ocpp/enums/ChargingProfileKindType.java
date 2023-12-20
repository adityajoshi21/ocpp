package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargingProfileKindType {
    Absolute("Absolute"),
    Recurring("Recurring"),
    Relative("Relative");
    private final String value;

    ChargingProfileKindType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChargingProfileKindType fromValue(String value) {
        for (ChargingProfileKindType enumValue : ChargingProfileKindType.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ChargingProfileKindType value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
