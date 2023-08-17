package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.RemoteStartStopStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteStopTransactionResponse {
    @NotNull
    private RemoteStartStopStatus status;
}
