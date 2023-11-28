package com.blucharge.ocpp.dto.authorize;
import com.blucharge.ocpp.dto.IdToken;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorizeRequest {
    private IdToken idTag;
}