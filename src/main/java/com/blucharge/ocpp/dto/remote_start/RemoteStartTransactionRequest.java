package com.blucharge.ocpp.dto.remote_start;

import com.blucharge.ocpp.dto.ChargingProfile;
import com.blucharge.ocpp.dto.IdToken;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemoteStartTransactionRequest {
    private Integer connectorId;
    private IdToken idTag;
    private ChargingProfile chargingProfile;
}
