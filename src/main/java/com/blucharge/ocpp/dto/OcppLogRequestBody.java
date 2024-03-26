package com.blucharge.ocpp.dto;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OcppLogRequestBody {
    private Long startTime;
    private Long endTime;
    private Long limit;
    private Long offset;
    private String chargeBoxId;
}
