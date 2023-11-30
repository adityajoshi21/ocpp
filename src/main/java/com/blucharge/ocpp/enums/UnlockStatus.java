package com.blucharge.ocpp.enums;

public enum UnlockStatus {
    UNLOCKED("Unlocked"),
    UNLOCK_FAILED("UnlockFailed"),
    NOT_SUPPORTED("NotSupported");

    private final String value;

    UnlockStatus(String value) {
        this.value = value;
    }
}
