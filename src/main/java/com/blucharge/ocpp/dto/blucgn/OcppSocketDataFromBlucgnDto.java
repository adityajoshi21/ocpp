package com.blucharge.ocpp.dto.blucgn;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OcppSocketDataFromBlucgnDto {
    private String inputType;
    private String chargeId;
    private String inputData;
}
