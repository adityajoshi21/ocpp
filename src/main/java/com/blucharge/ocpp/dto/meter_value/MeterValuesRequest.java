package com.blucharge.ocpp.dto.meter_value;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeterValuesRequest {
    private Integer connectorId;
    private Integer transactionId;
    private List<MeterValue> meterValue;
}
