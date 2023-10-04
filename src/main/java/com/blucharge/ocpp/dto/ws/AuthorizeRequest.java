package com.blucharge.ocpp.dto.ws;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthorizeRequest {
    private String idTag;
}