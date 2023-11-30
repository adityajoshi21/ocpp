package com.blucharge.ocpp.dto.stop_transaction;

import com.blucharge.ocpp.dto.IdTagInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StopTransactionResponse {
    private IdTagInfo idTagInfo;
}
