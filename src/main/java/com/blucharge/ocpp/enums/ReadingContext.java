package com.blucharge.ocpp.enums;

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
        return value;
    }

    ReadingContext(String v) {
        this.value = v;
    }

}


