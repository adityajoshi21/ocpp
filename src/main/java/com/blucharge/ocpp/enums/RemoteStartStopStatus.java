package com.blucharge.ocpp.enums;

public enum RemoteStartStopStatus {
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String value;

    RemoteStartStopStatus(String value) {
        this.value = value;
    }
}