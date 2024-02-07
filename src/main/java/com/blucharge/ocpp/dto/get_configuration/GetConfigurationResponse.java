package com.blucharge.ocpp.dto.get_configuration;

import com.blucharge.ocpp.dto.KeyValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetConfigurationResponse {
    private KeyValue configurationKey;
    private String unknownKey;
}

