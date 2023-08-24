package com.blucharge.ocpp.dto;

import lombok.*;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor

public class GetConfigResponse {

    private  List<ConfigurationKey> configurationKey;
    private  List<String> unknownKey;

}

