package com.blucharge.ocpp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeyValue {
    private String key;
    private Boolean readonly;
    private String value;
}
