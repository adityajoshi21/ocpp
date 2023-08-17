package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.RegistrationStatus;
import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
public class BootNotificationResponse
{
        @NotNull
        private String status;
        @NotNull
        private DateTime currentTime;
        @NotNull
        private Integer interval;

        public BootNotificationResponse withStatus(RegistrationStatus value) {
                setStatus(value.name());
                return this;
        }
        public BootNotificationResponse withCurrentTime(DateTime value) {
                setCurrentTime(value);
                return this;
        }

        public BootNotificationResponse withInterval(int value) {
                setInterval(value);
                return this;
        }
}