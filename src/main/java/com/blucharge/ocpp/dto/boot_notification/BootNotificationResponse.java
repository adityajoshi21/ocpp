package com.blucharge.ocpp.dto.boot_notification;

import com.blucharge.ocpp.enums.RegistrationStatus;
import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BootNotificationResponse {
    private DateTime currentTime;
    private Integer interval;
    private RegistrationStatus status;
}