package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.Measurand;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OcppLogData {
    List<Measurand> sampledValue;
    private Long transactionId;
    private Long connectorId;
    private String status;
    private String idTag;
    private Long meterStart;
    private String meterStop;
    private String reason;
    private String firmwareVersion;
    private String chargePointVendor;
}
