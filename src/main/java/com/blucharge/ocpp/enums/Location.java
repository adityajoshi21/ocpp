package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Location {
    BODY("Body"),
    CABLE("Cable"),
    EV("EV"),
    INLET("Inlet"),
    OUTLET("Outlet");

    private final String value;

    Location(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Location location(String value) {
        for (Location location : Location.values())
            if (location.value.equalsIgnoreCase(value))
                return location;
        throw new IllegalArgumentException("Invalid Location value: " + value);

    }

    public String value() {
        return this.value;
    }
}
