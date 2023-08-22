package com.blucharge.ocpp.enums;

public enum ValueFormat {
    RAW ("Raw"),
    SIGNED_DATA("Signed.Data");

    private final String value;
    public String value() {
        return  value;
    }
    ValueFormat (String value) {
        this.value= value;
    }
}