package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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

    @JsonCreator
    public static MessageTrigger fromValue(String value) {
        for (MessageTrigger enumValue : MessageTrigger.values()) {
            if (enumValue.value.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid Measurand value: " + value);
    }

    public String value() {
        return this.value;
    }
}
