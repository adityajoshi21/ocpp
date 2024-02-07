package com.blucharge.ocpp.dto.authorize;

import com.blucharge.ocpp.dto.IdTagInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorizeResponse {
    private IdTagInfo idTagInfo;
}