package com.blucharge.ocpp.dto.authorize;

import com.blucharge.ocpp.dto.IdToken;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorizeRequest {
    private IdToken idTag;
}