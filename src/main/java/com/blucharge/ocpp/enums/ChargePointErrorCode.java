package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargePointErrorCode {
    CONNECTOR_LOCK_FAILURE("Connector lock failure"),
    EV_COMMUNICATION_ERROR("Ev communication error"),
    GROUND_FAILURE("Ground failure"),
    HIGH_TEMPERATURE("High temperature"),
    INTERNAL_ERROR("Internal error"),
    LOCAL_LIST_CONFLICT("Local list conflict"),
    NO_ERROR("No error"),
    OTHER_ERROR("Other error"),
    OVER_CURRENT_FAILURE("Over current failure"),
    OVER_VOLTAGE("Over volume"),
    POWER_METER_FAILURE("Power meter failure"),
    POWER_SWITCH_FAILURE("Power switch failure"),
    READER_FAILURE("Reader failure"),
    RESET_FAILURE("Reset failure"),
    UNDER_VOLTAGE("Under voltage"),
    WEAK_SIGNAL("Weak signal"),
    EXT_URGENT_BTN_DOWN("Ext urgent btn down");
    private final String value;

    ChargePointErrorCode(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChargePointErrorCode fromValue(String value) {
        for (ChargePointErrorCode enumValue : ChargePointErrorCode.values()) {
            if (enumValue.value.replaceAll("_"," ").equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ChargePointErrorCode value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}


