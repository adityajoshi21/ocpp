package com.blucharge.ocpp.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartTransactionResponse{
    @NotNull
    private Long transactionId;
    private IdTagInfo idTagInfo;
    public StartTransactionResponse withTransactionId(Long value) {
        setTransactionId(value);
        return this;
    }

    public StartTransactionResponse withIdTagInfo(IdTagInfo value) {
        setIdTagInfo(value);
        return this;
    }
}
