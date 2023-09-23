package com.blucharge.ocpp.dto.api;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class GetConfigResponse {

    private  List<ConfigurationKey> configurationKey;
    private  List<String> unknownKey;

}

