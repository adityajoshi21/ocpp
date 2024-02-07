package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UnitOfMeasure {
    Wh("Wh"),
    kWh("kWh");
    private final String value;

    UnitOfMeasure(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UnitOfMeasure fromValue(String value) {
        for (UnitOfMeasure enumValue : UnitOfMeasure.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid UnitOfMeasure value: " + value);
    }

    public String value() {
        return this.value;
    }
}
