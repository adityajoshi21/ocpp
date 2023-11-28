package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.api.*;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationRequest;
import com.blucharge.ocpp.dto.boot_notification.BootNotificationResponse;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatRequest;
import com.blucharge.ocpp.dto.heartbeat.HeartbeatResponse;
import com.blucharge.ocpp.enums.*;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.blucharge.ocpp.constants.StringConstant.HEART_BEAT_INTERVAL;


@Slf4j
@Service
public class ChargerServiceImpl implements ChargerService {
    @Autowired
    private ChargerRepository chargerRepository;

    @Autowired
    private ConnectorRepository connectorRepository;
    @Override
    public BootNotificationResponse insertBootNotification(BootNotificationRequest parameters, String chargerName) {
        Boolean isRegistered = isChargerRegistered(chargerName);

        if (Boolean.TRUE.equals(isRegistered)) {
            chargerRepository.updateBootNotificationForCharger(parameters, chargerName);
        } else {
            log.info("The charger is not registered with blucharge"+ chargerName);
        }

        return new BootNotificationResponse(
                Boolean.TRUE.equals(isRegistered) ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED,
                DateTime.now(),
                HEART_BEAT_INTERVAL
        );
    }

    @Override
    public HeartbeatResponse insertHeartbeat(HeartbeatRequest request, String chargerName) {
        ChargerRecord chargerRecord = chargerRepository.getChargerRecordFromName(chargerName);
        chargerRepository.updateChargerHeartBeat(chargerRecord.getId(), DateTime.now());
        List<ConnectorRecord> connectorRecords = connectorRepository.getConnectorRecordForChargerId(chargerRecord.getId());
        for(ConnectorRecord connectorRecord:connectorRecords){
            connectorRepository.updateConnectorHeartBeat(connectorRecord.getId(),DateTime.now());
        }
        return new HeartbeatResponse(DateTime.now());
    }

    @Override
    public Boolean isChargerRegistered(String chargerName) {
        ChargerRecord chargerRecord = chargerRepository.getChargerRecordFromName(chargerName);
        return !Objects.isNull(chargerRecord);
    }

    @Override
    public GetConfigResponse getConfiguration(GetConfigRequest getConfigRequest, String chargerName) {

        Boolean doesChargerExist = isChargerRegistered(chargerName);
        if(Boolean.FALSE.equals(doesChargerExist)){
            log.error("Charger {} doesn't exist for which we are seeking configurations", chargerName);
        }

        List<String> keys = getConfigRequest.getKey();
        List<ConfigurationKey> recognizedKeys = new ArrayList<>();
        List<String> unknownKeys = new ArrayList<>();
            if(!keys.isEmpty()) {     //If list in request is null or empty return all configs
                for(String key : keys){
                    if(true) //check if key is recognised for CP or not, todo : how to fetch this for a charger?
                    recognizedKeys.add(createConfigurationSetting(key));
                    else
                        unknownKeys.add(key);
                }
                return new GetConfigResponse(recognizedKeys, unknownKeys);

            }
            return new GetConfigResponse(recognizedKeys, unknownKeys);
    }

    @Override
    public ChangeConfigResponse changeConfiguration(ChangeConfigRequest changeConfigRequest, String chargerName) {
        // TODO: How to fetch all configs of a charger?
        //Fetch list/map of all configs of a charger

        //See if accessed config for the charger exists in the map, update if exists     {for which we use .containsKey} if boolean value is true put the value for the key,
        // else return not supported

        return null;
    }

    private ConfigurationKey createConfigurationSetting(String key) {
        String value = "Test Value";
        Boolean readOnly = true;
        return  new ConfigurationKey(key,value,readOnly);
    }

    @Override
    public TriggerMessageResponse triggerMessage(TriggerMessageRequest triggerMessageRequest, String chargerName) {
        TriggerMessageResponse triggerMessageResponse = new TriggerMessageResponse();
        Boolean flag = MessageTrigger.checkForValidEnum(triggerMessageRequest.getRequestedMessage());
        triggerMessageResponse.setStatus(Boolean.TRUE.equals(flag) ? TriggerMessageStatus.ACCEPTED : TriggerMessageStatus.REJECTED);
        if(Boolean.TRUE.equals(flag)){
            MessageTrigger requestedMessage = triggerMessageRequest.getRequestedMessage();
            Boolean isConnectorIdRequired = requestedMessage.isConnectorIdRequired();
            if(Boolean.FALSE.equals(isConnectorIdRequired)) {
                return triggerMessageResponse;
            }
                if(isConnectorIdRequired && !Objects.isNull(triggerMessageRequest.getConnectorId())){
                return  triggerMessageResponse;
            }
        }
        return null;
    }
}

