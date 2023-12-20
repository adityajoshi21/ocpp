package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TriggerMessageStatus {
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NOT_IMPLEMENTED("Not implemented");

    private final String value;

    TriggerMessageStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TriggerMessageStatus fromValue(String value) {
        for (TriggerMessageStatus enumValue : TriggerMessageStatus.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid TriggerMessageStatus value: " + value);
    }

    public String value() {
        return this.value;
    }
}
