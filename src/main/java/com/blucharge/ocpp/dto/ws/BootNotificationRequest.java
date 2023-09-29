package com.blucharge.ocpp.dto.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
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
