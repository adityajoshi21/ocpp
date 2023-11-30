package com.blucharge.ocpp.dto.api;

import com.blucharge.ocpp.enums.UnlockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlockConnectorResponse {
    private UnlockStatus status;
}
