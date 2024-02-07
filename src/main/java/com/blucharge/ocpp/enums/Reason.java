package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Reason {
    EmergencyStop("Emergency Stop"),
    PowerLoss("Power Loss"),
    Reboot("Reboot");
    private final String value;

    Reason(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Reason fromValue(String value) {
        for (Reason reason : Reason.values())
            if (reason.value.equalsIgnoreCase(value))
                return reason;
        throw new IllegalArgumentException("Invalid Reading Context value: " + value);
    }

    public String value() {
        return this.value;
    }
}
