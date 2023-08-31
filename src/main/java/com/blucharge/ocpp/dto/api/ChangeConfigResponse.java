package com.blucharge.ocpp.dto.api;

import com.blucharge.ocpp.enums.ConfigurationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeConfigResponse {
    @NotNull(message = "Status not found")
    private ConfigurationStatus status;
}
