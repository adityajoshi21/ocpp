package com.blucharge.ocpp.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeConfigRequest {
    @NotNull(message = "Key missing in change config request")
    private String key;
    @NotNull(message = "Value missing in change config request")
    private String value;
}
