package com.blucharge.ocpp.dto.start_transaction;

import com.blucharge.ocpp.dto.IdTagInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StartTransactionResponse {
    private IdTagInfo idTagInfo;
    private Integer transactionId;
}
