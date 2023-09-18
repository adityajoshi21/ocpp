package com.blucharge.ocpp.dto.ws;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BootNotificationRequest {
    @NotNull (message = "Charger vendor not found in request")
    private String chargePointVendor;
    @NotNull (message = "Charger Model not found in request")
    private String chargePointModel;
    private String chargePointSerialNumber;
    private String chargeBoxSerialNumber;
    private String firmwareVersion;
    private String meterSerialNumber;
    private String meterType;
    private String imsi;
    private String iccid;

}