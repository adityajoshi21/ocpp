package com.blucharge.ocpp.enums;


public enum ConnectorStatus {
    AVAILABLE("Available"),
    CHARGING("Charging"),
    PREPARING("Preparing"),
    SUSPENDED_EV("SuspendedEV"),
    SUSPENDED_EVSE("SuspendedEVSE"),
    FINISHING("Finishing"),
    RESERVED("Reserved"),
    FAULTED("Faulted"),
    UNAVAILABLE("Unavailable"),
    EXT_URGENT_BTN_DOWN("EXT.UrgentBtnDown");

    private final String value;

    ConnectorStatus(String v) {
        this.value = v;
    }
}

