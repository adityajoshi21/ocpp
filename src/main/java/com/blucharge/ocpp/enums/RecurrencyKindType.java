package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecurrencyKindType {
    Daily("Daily"),
    Weekly("Weekly");
    private final String value;

    RecurrencyKindType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RecurrencyKindType fromValue(String value) {
        for (RecurrencyKindType enumValue : RecurrencyKindType.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid Recurrency Kind Type value: " + value);
    }

    public String value() {
        return this.value;
    }
}
