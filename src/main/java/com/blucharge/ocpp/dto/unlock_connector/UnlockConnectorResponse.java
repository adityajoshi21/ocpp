package com.blucharge.ocpp.dto.unlock_connector;

import com.blucharge.ocpp.enums.UnlockStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UnlockConnectorResponse {
    private UnlockStatus status;
}
