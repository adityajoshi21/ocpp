package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConfigurationStatus {
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    REBOOT_REQUIRED("Reboot Required"),
    NOT_SUPPORTED("Not supported");
    private final String value;

    ConfigurationStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ConfigurationStatus fromValue(String value) {
        for (ConfigurationStatus enumValue : ConfigurationStatus.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid ConfigurationStatus value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
