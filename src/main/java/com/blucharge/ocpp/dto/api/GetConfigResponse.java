package com.blucharge.ocpp.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class GetConfigResponse {

    private List<ConfigurationKey> configurationKey;
    private List<String> unknownKey;

}

