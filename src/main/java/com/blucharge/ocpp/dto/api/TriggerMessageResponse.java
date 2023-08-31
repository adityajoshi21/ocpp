package com.blucharge.ocpp.dto.api;

import com.blucharge.ocpp.enums.TriggerMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerMessageResponse {
    @NotNull(message = "Trigger message status not found in response")
    private TriggerMessageStatus status;
}
