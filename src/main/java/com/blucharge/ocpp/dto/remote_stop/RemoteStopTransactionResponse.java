package com.blucharge.ocpp.dto.remote_stop;

import com.blucharge.ocpp.enums.RemoteStartStopStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemoteStopTransactionResponse {
    private RemoteStartStopStatus status;
}
