package com.blucharge.ocpp.dto.ws;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthorizeRequest {
    @NotNull (message = "IdTag is missing in request")
    private String idTag;
}