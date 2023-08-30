package com.blucharge.ocpp.enums;

public enum MessageTrigger {

    BOOT_NOTIFICATION("BootNotification"),
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification"),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification"),
    HEARTBEAT("Heartbeat"),
    METER_VALUES("MeterValues"),
    STATUS_NOTIFICATION("StatusNotification");

    private final String value;

    MessageTrigger(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}