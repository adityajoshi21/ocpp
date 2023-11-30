package com.blucharge.ocpp.dto.remote_stop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemoteStopTransactionRequest {
    private Integer transactionId;
}
