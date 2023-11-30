package com.blucharge.ocpp.enums;

import com.blucharge.ocpp.constants.ApplicationConstants;

public enum MessageTrigger {
    BOOT_NOTIFICATION("BootNotification", false),  //here flag denotes if connectorId is needed or not
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification", false),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification", false),
    HEARTBEAT("Heartbeat", false),
    METER_VALUES("MeterValues", true),
    STATUS_NOTIFICATION("StatusNotification", true);

    private final String value;
    private final Boolean isConnectorIdRequired;

    MessageTrigger(String value, Boolean flag) {
        this.value = value;
        this.isConnectorIdRequired = flag;
    }

    public static Boolean checkForValidEnum(MessageTrigger messageTrigger) {
        return ApplicationConstants.TRIGGER_MESSAGE.contains(messageTrigger);
    }

    public String getValue() {
        return value;
    }

    public Boolean isConnectorIdRequired() {
        return isConnectorIdRequired;
    }
}
