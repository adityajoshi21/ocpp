package com.blucharge.ocpp.dto.ws;

import lombok.Data;
import org.joda.time.DateTime;

    import javax.validation.constraints.NotNull;

@Data
public class StatusNotificationRequest{
    @NotNull (message = "Connector Id not found in request")
    private Integer connectorId;
    @NotNull (message = "Status not found in Status Notification request")
    private String status;
    @NotNull (message = "Error code not found in request")
    private String errorCode;
    private String errorInfo;
    private DateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }


}
