package com.blucharge.ocpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteStartTransactionRequest {
    @NotNull
    private String idTag;
    private String connectorName;
    private String userId;
}
