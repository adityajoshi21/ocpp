package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.RemoteStartStopStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteStartTransactionResponse {
    private RemoteStartStopStatus status;
}
