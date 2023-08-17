package com.blucharge.ocpp.enums;

public enum Location {
    BODY("Body"),
    CABLE("Cable"),
    EV("EV"),
    INLET("Inlet"),
    OUTLET("Outlet");

    private final String value;
    public String value() {
        return value;
    }

    Location(String v) {
        this.value = v;
    }
}
