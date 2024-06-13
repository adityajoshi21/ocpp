package com.blucharge.ocpp.dto;

import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LogTempDataInsertRequestDto {
    private DateTime timestamp;
}
