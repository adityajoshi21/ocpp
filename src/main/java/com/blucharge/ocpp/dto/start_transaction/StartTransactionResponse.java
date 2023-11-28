package com.blucharge.ocpp.dto.start_transaction;

import com.blucharge.ocpp.dto.ws.IdTagInfo;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StartTransactionResponse{
    private Integer transactionId;
    private IdTagInfo idTagInfo;
}
