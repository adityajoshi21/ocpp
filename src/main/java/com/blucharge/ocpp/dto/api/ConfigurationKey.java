package com.blucharge.ocpp.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationKey {
    private  String key;
    private  String value;
    private  Boolean readonly;
}
