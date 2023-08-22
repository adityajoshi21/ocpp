package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.AuthorizationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTagInfo {
    protected AuthorizationStatus status;
    protected DateTime expiryDate;
    protected String parentIdTag;
}