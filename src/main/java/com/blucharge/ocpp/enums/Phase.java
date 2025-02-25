package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Phase {
    L_1("L1"),
    L_2("L2"),
    L_3("L3"),
    N("N"),
    L_1_N("L1-N"),
    L_2_N("L2-N"),
    L_3_N("L3-N"),
    L_1_L_2("L1-L2"),
    L_2_L_3("L2-L3"),
    L_3_L_1("L3-L1");
    private final String value;

    Phase(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Phase fromValue(String value) {
        for (Phase phase : Phase.values())
            if (phase.value.equalsIgnoreCase(value))
                return phase;
        throw new IllegalArgumentException("Invalid Phase value: " + value);

    }

    public String value() {
        return this.value;
    }
}