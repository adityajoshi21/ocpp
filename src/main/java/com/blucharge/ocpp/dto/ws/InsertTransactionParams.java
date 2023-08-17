package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.TransactionStatusUpdate;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Getter
@Builder
public class InsertTransactionParams {
    private  Integer connectorId;
    private  String  idTag;
    private  DateTime startTimestamp;
    private  BigDecimal startMeterValue;
    private  final TransactionStatusUpdate statusUpdate = TransactionStatusUpdate.AfterStart;

    //private final Long reservationId;
}
