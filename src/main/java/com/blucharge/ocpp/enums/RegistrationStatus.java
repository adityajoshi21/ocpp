package com.blucharge.ocpp.enums;

public enum RegistrationStatus {
    ACCEPTED("Accepted"),
    PENDING("Pending"),
    REJECTED("Rejected");
    private final String value;

    RegistrationStatus(String value) {
        this.value = value;
    }

}