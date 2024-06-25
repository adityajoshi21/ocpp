package com.blucharge.ocpp.dto;

import lombok.*;
import org.joda.time.DateTime;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3DataInsertRequestDto {
    private DateTime startTime;
    private DateTime endTime;
    private String name;
}
