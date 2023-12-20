package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RemoteStartStopStatus {
    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    private final String value;

    RemoteStartStopStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RemoteStartStopStatus fromValue(String value) {
        for (RemoteStartStopStatus enumValue : RemoteStartStopStatus.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid RemoteStartStopStatus value: " + value);
    }

    public String value() {
        return this.value;
    }
}