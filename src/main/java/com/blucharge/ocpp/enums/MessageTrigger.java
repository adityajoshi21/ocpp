package com.blucharge.ocpp.enums;

import com.blucharge.ocpp.constants.ApplicationConstants;

public enum MessageTrigger {
    BOOT_NOTIFICATION("BootNotification", false),
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification", false),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification", false),
    HEARTBEAT("Heartbeat", false),
    METER_VALUES("MeterValues", true),
    STATUS_NOTIFICATION("StatusNotification", true);



    private final String value;
    private final Boolean isRequired;
    MessageTrigger(String value,Boolean flag) {
        this.value = value;
        this.isRequired = flag;
    }

    public String getValue() {
        return value;
    }
    public Boolean getIsRequired(){return isRequired;}

    public static Boolean checkForValidEnum(MessageTrigger messageTrigger){
        return ApplicationConstants.TRIGGER_MESSAGES.contains(messageTrigger);
    }
}
