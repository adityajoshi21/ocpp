package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.OcppTagRecord;
import com.blucharge.ocpp.dto.ws.AuthorizeRequest;
import com.blucharge.ocpp.dto.ws.AuthorizeResponse;
import com.blucharge.ocpp.dto.ws.IdTagInfo;
import com.blucharge.ocpp.enums.AuthorizationStatus;
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
    @Autowired private OcppTagRepository ocppTagRepository;
    //private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(1000);

    @Override
    public IdTagInfo getIdTagInfo(String idTag) {
        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(idTag);
        IdTagInfo idTagInfo = new IdTagInfo();


        if (Objects.isNull(ocppTagRecord)) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(AuthorizationStatus.INVALID);
            // unknownChargePointService.processNewUnidentified(idTag);
        } else {

            //When Authorisation status for user is blocked
            if(Boolean.TRUE.equals(ocppTagRecord.getBlocked())) {
                log.error("The user with the idTag {} is blocked!", idTag);
                idTagInfo.setStatus(AuthorizationStatus.BLOCKED);
            }
            DateTime expiryDate = ocppTagRecord.getExpiryOn();
            boolean isExpiryDateSet = expiryDate != null;

            //When Authorisation status for the user has expired
            if (isExpiryDateSet && DateTime.now().isAfter(ocppTagRecord.getExpiryOn())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.EXPIRED);
            }
            else {
                log.info("The user with idTag '{}' is ACCEPTED!", idTag);
                idTagInfo.setStatus(AuthorizationStatus.ACCEPTED);
                //If Expiry Timestamp is set for user in the db, use it. Else update it to 1 hour from currentTime
                DateTime expiry = isExpiryDateSet? expiryDate : DateTime.now().plusHours(1) ; //Move this later to application CONSTANTS
                idTagInfo.setExpiryDate(expiry);
                idTagInfo.setParentIdTag("0");
            }
        }
        return idTagInfo;
    }


    @Override
    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargerIdentity) {
        String idTag = parameters.getIdTag();
        IdTagInfo idTagInfo = getIdTagInfo(idTag);
        OcppTagRecord ocppTagRecord = ocppTagRepository.getRecord(parameters.getIdTag());
        if(!Objects.isNull(ocppTagRecord)) {
            ocppTagRecord.setExpiryOn(idTagInfo.getExpiryDate());
            ocppTagRecord.setParentTag(idTagInfo.getParentIdTag());
            ocppTagRecord.store();
        }

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
    }
}