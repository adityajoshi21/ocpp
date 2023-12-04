package com.blucharge.ocpp.dto.trigger_message;

import com.blucharge.ocpp.enums.TriggerMessageStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerMessageResponse {
    private TriggerMessageStatus status;
}
