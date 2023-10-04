package com.blucharge.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReadingContext {
    INTERRUPTION_BEGIN("Interruption.Begin"),
    INTERRUPTION_END("Interruption.End"),
    OTHER("Other"),
    SAMPLE_CLOCK("Sample.Clock"),
    SAMPLE_PERIODIC("Sample.Periodic"),
    TRANSACTION_BEGIN("Transaction.Begin"),
    TRANSACTION_END("Transaction.End"),
    TRIGGER("Trigger");

    private final String value;
    public String value() {
        return this.value;
    }

    ReadingContext(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ReadingContext fromValue (String value) {
        for(ReadingContext readingContext : ReadingContext.values())
            if(readingContext.value.equalsIgnoreCase(value))
                return readingContext;
        throw new IllegalArgumentException("Invalid Reading Context value: " + value);

    }

}


