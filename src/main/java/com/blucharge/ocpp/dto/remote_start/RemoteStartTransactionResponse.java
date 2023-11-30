package com.blucharge.ocpp.dto.remote_start;

import com.blucharge.ocpp.enums.RemoteStartStopStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemoteStartTransactionResponse {
    private RemoteStartStopStatus status;
}
