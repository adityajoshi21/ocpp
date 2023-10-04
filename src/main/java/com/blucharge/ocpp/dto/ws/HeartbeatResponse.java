package com.blucharge.ocpp.dto.ws;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
public class HeartbeatResponse {
    private DateTime currentTime;


    public HeartbeatResponse withCurrentTime(DateTime value) {
        setCurrentTime(value);
        return this;
    }
}