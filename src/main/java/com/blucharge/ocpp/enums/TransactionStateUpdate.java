package com.blucharge.ocpp.enums;

import lombok.Getter;

@Getter
public enum TransactionStateUpdate {
    AfterStart(ConnectorStatus.CHARGING),
    AfterStop(ConnectorStatus.FINISHING);

    private final String status;
    private final String errorCode = ChargePointErrorCode.NO_ERROR.name();

    TransactionStateUpdate(ConnectorStatus status) {
        this.status = status.name();
    }
}
