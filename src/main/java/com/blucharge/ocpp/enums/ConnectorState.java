package com.blucharge.ocpp.enums;

public enum ConnectorState {

    WAITING_FOR_CHARGER_RESPONSE("Waiting For Charger Response"),
    CHARGING ("Charging"),
    STOPPING ("Stopping"),
    IDLE ("Idle"),
    RESERVED ("Reserved");

    private final String value;

    ConnectorState(String v) {
        this.value = v;
    }
}
