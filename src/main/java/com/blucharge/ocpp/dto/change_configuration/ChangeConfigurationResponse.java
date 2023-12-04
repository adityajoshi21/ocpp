package com.blucharge.ocpp.dto.change_configuration;

import com.blucharge.ocpp.enums.ConfigurationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeConfigurationResponse {
    private ConfigurationStatus status;
}
