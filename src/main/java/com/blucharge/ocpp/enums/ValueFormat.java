package com.blucharge.ocpp.enums;

import com.google.gson.annotations.SerializedName;

public enum ValueFormat {
    RAW ("Raw"),
    SIGNED_DATA("Signed.Data");

    private final String value;
    public String value() {
        return  this.name();
    }
    ValueFormat (String value) {
        this.value= value;
    }
}