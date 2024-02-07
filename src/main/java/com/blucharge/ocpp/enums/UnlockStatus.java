package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UnlockStatus {
    UNLOCKED("Unlocked"),
    UNLOCK_FAILED("UnlockFailed"),
    NOT_SUPPORTED("NotSupported");

    private final String value;

    UnlockStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UnlockStatus fromValue(String value) {
        for (UnlockStatus enumValue : UnlockStatus.values())
            if (enumValue.value.equalsIgnoreCase(value))
                return enumValue;
        throw new IllegalArgumentException("Invalid UnlockStatus value: " + value);
    }

    public String value() {
        return this.value;
    }
}
