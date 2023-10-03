package com.blucharge.ocpp.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteStartTransactionRequest {
    private String idTag;
    private Integer connectorId;
}
