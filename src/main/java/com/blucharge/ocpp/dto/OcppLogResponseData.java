package com.blucharge.ocpp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OcppLogResponseData {
    private Long id;
    private String messageName;
    private String chargeBoxId;
    private Long createdOn;
    private String dataJson;
}
