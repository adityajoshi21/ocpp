package com.blucharge.ocpp.enums;

public enum ConfigurationStatus {
    ACCEPTED("Accepted"),
    REJECTED ("Rejected"),
    REBOOT_REQUIRED("Reboot Required"),
    NOT_SUPPORTED("Not supported");
    private String value;
    ConfigurationStatus(String val){
        this.value= val;
    }
}
