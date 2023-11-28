package com.blucharge.ocpp.dto.heartbeat;

import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HeartbeatResponse {
    private DateTime currentTime;
}