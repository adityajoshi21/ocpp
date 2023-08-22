package com.blucharge.ocpp.dto;

import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

//Some alien UFO stuff going on here

@ToString
@Getter
public class UnidentifiedIncomingObject {

    private final String key;
    private int numberOfAttempts = 0;
    private DateTime lastAttemptTimestamp;

    public UnidentifiedIncomingObject(String key) {
        this.key = key;
    }

    public synchronized void updateStats() {
        numberOfAttempts++;
        lastAttemptTimestamp = DateTime.now();
    }
}
