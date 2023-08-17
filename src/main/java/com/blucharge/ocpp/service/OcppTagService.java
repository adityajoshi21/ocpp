package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.ws.AuthorizeRequest;
import com.blucharge.ocpp.dto.ws.AuthorizeResponse;
import com.blucharge.ocpp.dto.ws.IdTagInfo;
import org.joda.time.DateTime;

public interface OcppTagService {
     IdTagInfo getIdTagInfo(String idTag);
     IdTagInfo getIdTagInfo(String idTag, String chargerId, DateTime transactionTime);
    AuthorizeResponse authorize(AuthorizeRequest parameters, String chargerIdentity);

}
