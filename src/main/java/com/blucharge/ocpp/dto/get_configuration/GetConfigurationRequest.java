package com.blucharge.ocpp.dto.get_configuration;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetConfigurationRequest {
    private String key;
}