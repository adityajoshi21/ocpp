package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ValueFormat {
    RAW("Raw"),
    SIGNED_DATA("SignedData");

    private final String value;

    ValueFormat(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ValueFormat fromValue(String value) {
        for (ValueFormat valueFormat : ValueFormat.values())
            if (valueFormat.value.equalsIgnoreCase(value))
                return valueFormat;
        throw new IllegalArgumentException("Invalid ValueFormat value: " + value);
    }

    public String value() {
        return this.value;
    }
}