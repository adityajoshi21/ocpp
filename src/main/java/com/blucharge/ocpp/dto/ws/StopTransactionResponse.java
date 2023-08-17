package com.blucharge.ocpp.dto.ws;

import lombok.Data;

@Data
public class StopTransactionResponse{

    private IdTagInfo idTagInfo;

    public StopTransactionResponse withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }
}
