package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RegistrationStatus {
    ACCEPTED("Accepted"),
    PENDING("Pending"),
    REJECTED("Rejected");
    private final String value;

    RegistrationStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RegistrationStatus fromValue(String value) {
        for (RegistrationStatus enumValue : RegistrationStatus.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid RegistrationStatus value: " + value);
    }

    public String value() {
        return this.value;
    }
}