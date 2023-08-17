package com.blucharge.ocpp.dto.ws;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
public class StatusNotificationRequest{
    @NotNull
    private Integer connectorId;
    @NotNull
    private String status;
    @NotNull
    private String errorCode;
    private String errorInfo;
    private DateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }


}
