package com.blucharge.ocpp.dto.api;

import com.blucharge.ocpp.enums.MessageTrigger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerMessageRequest {
    @NotNull(message = "Request message found to be missing in request")
    private MessageTrigger requestedMessage;
    private Integer connectorId;

    public TriggerMessageRequest withRequestedMessage(MessageTrigger value) {
        setRequestedMessage(value);
        return this;
    }

}
