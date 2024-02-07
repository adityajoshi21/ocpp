package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargePointStatus {
    AVAILABLE("Available"),
    CHARGING("Charging"),
    PREPARING("Preparing"),
    SUSPENDED_EV("SuspendedEV"),
    SUSPENDED_EVSE("SuspendedEVSE"),
    FINISHING("Finishing"),
    RESERVED("Reserved"),
    FAULTED("Faulted"),
    UNAVAILABLE("Unavailable"),
    EXT_URGENT_BTN_DOWN("EXT.UrgentBtnDown");
    private final String value;

    ChargePointStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChargePointStatus fromValue(String value) {
        for (ChargePointStatus enumValue : ChargePointStatus.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ChargePointStatus value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
