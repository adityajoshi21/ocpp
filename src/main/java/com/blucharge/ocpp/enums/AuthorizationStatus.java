package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorizationStatus {
    ACCEPTED("Accepted"),
    BLOCKED("Blocked"),
    EXPIRED("Expired"),
    INVALID("Invalid");
    private final String value;

    AuthorizationStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AuthorizationStatus fromValue(String value) {
        for (AuthorizationStatus enumValue : AuthorizationStatus.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid AuthorizationStatus value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}