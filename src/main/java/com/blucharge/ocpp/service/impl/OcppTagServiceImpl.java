package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.ocpp.dto.IdToken;
import com.blucharge.ocpp.dto.authorize.AuthorizeRequest;
import com.blucharge.ocpp.dto.authorize.AuthorizeResponse;
import com.blucharge.ocpp.dto.ws.IdTagInfo;
import com.blucharge.ocpp.enums.AuthorizationStatus;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.OcppTagRepository;
import com.blucharge.ocpp.service.OcppTagService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
public class OcppTagServiceImpl implements OcppTagService {
    @Autowired
    private OcppTagRepository ocppTagRepository;
    @Autowired
    private ChargerRepository chargerRepository;

    @Override
    public AuthorizeResponse checkUserAuth(AuthorizeRequest parameters, String chargerName) {
        OcppTagRecord ocppTagRecord = ocppTagRepository.getOcppTagRecordForIdTag(parameters.getIdTag().getIdToken());
        if(Objects.isNull(ocppTagRecord)){
            return new AuthorizeResponse(
                    new IdTagInfo(
                            DateTime.now(),
                            null,
                            AuthorizationStatus.INVALID
                    )
            );
        }
        if ("BLOCKED".equals(ocppTagRecord.getState())){
            return new AuthorizeResponse(
                    new IdTagInfo(
                            DateTime.now(),
                            null,
                            AuthorizationStatus.BLOCKED
                    )
            );
        }
        if(Objects.nonNull(ocppTagRecord.getExpiryOn()) && ocppTagRecord.getExpiryOn().getMillis() < DateTime.now().getMillis()){
            return new AuthorizeResponse(
                    new IdTagInfo(
                            DateTime.now(),
                            null,
                            AuthorizationStatus.EXPIRED
                    )
            );
        }
        if("OTP".equals(ocppTagRecord.getTagMappingType()) || "RFID".equals(ocppTagRecord.getTagMappingType())){
            ChargerRecord chargerRecord = chargerRepository.getChargerRecordFromName(chargerName);
            if(!ocppTagRecord.getChargerId().equals(chargerRecord.getId())){
                return new AuthorizeResponse(
                        new IdTagInfo(
                                DateTime.now(),
                                null,
                                AuthorizationStatus.INVALID
                        )
                );
            }
        }
        return new AuthorizeResponse(
                new IdTagInfo(
                        DateTime.now(),
                        new IdToken(ocppTagRecord.getParentTag()),
                        AuthorizationStatus.ACCEPTED
                )
        );
    }
}