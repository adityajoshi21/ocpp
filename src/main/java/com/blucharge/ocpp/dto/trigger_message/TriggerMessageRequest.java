package com.blucharge.ocpp.dto.trigger_message;

import com.blucharge.ocpp.enums.MessageTrigger;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerMessageRequest {
    private MessageTrigger requestedMessage;
    private Integer connectorId;
}
