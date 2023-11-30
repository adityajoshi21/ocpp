package com.blucharge.ocpp.dto.status_notification;

import com.blucharge.ocpp.enums.ChargePointErrorCode;
import com.blucharge.ocpp.enums.ChargePointStatus;
import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatusNotificationRequest {
    private Integer connectorId;
    private ChargePointErrorCode errorCode;
    private String info;
    private ChargePointStatus status;
    private DateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;
}
