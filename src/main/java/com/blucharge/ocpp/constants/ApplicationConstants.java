package com.blucharge.ocpp.constants;

import com.blucharge.ocpp.enums.MessageTrigger;

import java.util.Arrays;
import java.util.List;

public class ApplicationConstants {
    public static final String APPLICATION_ID = "OCPP";
    public static final String CLIENT_ID = "blusmart-ocpp";
    public static final String CLIENT_SECRET = "dd6e883f72539938e831f3ec2f4b312f";
    public static final String USERNAME = "ocpp-backend-user";
    public static final String PASSWORD = "Disp@7chBluSm@rt";
    public static  final String TEST_CHARGER = "chargepoint0234";
    public static  final Integer HEARTBEAT_INTERVAL = 60;   //Heartbeat Interval set to 60sec
    public static final List<MessageTrigger> TRIGGER_MESSAGES = Arrays.asList(MessageTrigger.HEARTBEAT, MessageTrigger.BOOT_NOTIFICATION, MessageTrigger.STATUS_NOTIFICATION, MessageTrigger.METER_VALUES, MessageTrigger.DIAGNOSTICS_STATUS_NOTIFICATION, MessageTrigger.FIRMWARE_STATUS_NOTIFICATION);
}

