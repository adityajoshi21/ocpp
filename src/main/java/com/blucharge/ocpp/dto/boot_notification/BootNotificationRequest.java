package com.blucharge.ocpp.dto.boot_notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BootNotificationRequest {
    private String chargePointVendor;
    private String chargePointModel;
    private String chargePointSerialNumber;
    private String chargeBoxSerialNumber;
    private String firmwareVersion;
    private String meterSerialNumber;
    private String meterType;
    private String imsi;
    private String iccid;
}
