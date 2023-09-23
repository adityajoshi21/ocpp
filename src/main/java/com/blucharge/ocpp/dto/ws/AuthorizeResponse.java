package com.blucharge.ocpp.dto.ws;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthorizeResponse {
    private IdTagInfo idTagInfo;

    public AuthorizeResponse withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }
}