package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.dto.IdToken;
import com.blucharge.ocpp.enums.AuthorizationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTagInfo {
    private DateTime expiryDate;
    private IdToken parentIdTag;
    private AuthorizationStatus status;
}