package com.blucharge.ocpp.dto.change_configuration;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeConfigurationRequest {
    private String key;
    private String value;
}
