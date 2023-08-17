package com.blucharge.ocpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargerRequest {
    private String chargerId;
    private  String endPointUrl;
    private String ocppVersion;
    private Long lastHeartbeatOn;
    private Integer noOfConnectors;
    private  String chargePointVendor;
    private  String chargePointModel;
    private  String chargePointSerialNumber;
    private  String chargeBoxSerialNumber;
    private  String firmwareVersion;
    private Long firmwareUpdateStatus;
    private Long diagnosticsOn;
    private  String diagnosticStatus;
    private  String iccid;
    private  String imsi;
    private  String meterType;
    private  String meterSerialNumber;
    private Long locationId;
    private Long requestId;
    private  String uuid;
}
