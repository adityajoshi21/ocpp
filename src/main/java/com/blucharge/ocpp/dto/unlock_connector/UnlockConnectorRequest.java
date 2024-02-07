package com.blucharge.ocpp.dto.unlock_connector;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UnlockConnectorRequest {
    private Integer connectorId;
}
