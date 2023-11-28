package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.authorize.AuthorizeRequest;
import com.blucharge.ocpp.dto.authorize.AuthorizeResponse;
import com.blucharge.ocpp.dto.ws.IdTagInfo;

public interface OcppTagService {
    AuthorizeResponse checkUserAuth(AuthorizeRequest parameters, String chargerName);
}
