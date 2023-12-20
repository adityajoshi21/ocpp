package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargingProfilePurposeType {
    ChargePointMaxProfile("Charge Point Max Profile");
    private final String value;

    ChargingProfilePurposeType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChargingProfilePurposeType fromValue(String value) {
        for (ChargingProfilePurposeType enumValue : ChargingProfilePurposeType.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ChargingProfilePurposeType value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
