package com.blucharge.ocpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteStopTransactionRequest {
    @NotNull
    private Long transactionId;

}
