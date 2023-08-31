package com.blucharge.ocpp.enums;

public enum TriggerMessageStatus {
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NOT_IMPLEMENTED ("Not implemented");

    private final String value;

    private TriggerMessageStatus(String value) {
        this.value = value;
    }
}
