package com.blucharge.ocpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorRequest {
    private Integer connectorNumber;
    private Long chargerId;
    private String displayName;
    private String evseId;
    private String connectorType;
    private String status;
    private Long statusNotificationOn;
    private String state;
    private String errorCode;
    private String errorInfo;
    private String vendorErrorCode;
    private Long requestId;
    private String chargerType;
    private String uuid;
    private Boolean isActive;
}
