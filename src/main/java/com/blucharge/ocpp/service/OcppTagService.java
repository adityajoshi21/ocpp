package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.authorize.AuthorizeRequest;
import com.blucharge.ocpp.dto.authorize.AuthorizeResponse;

public interface OcppTagService {
    AuthorizeResponse checkUserAuth(AuthorizeRequest parameters, String chargerName);
}
