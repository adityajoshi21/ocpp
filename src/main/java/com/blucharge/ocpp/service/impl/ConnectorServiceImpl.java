package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
import com.blucharge.ocpp.dto.ws.StatusNotificationRequest;
import com.blucharge.ocpp.dto.ws.StatusNotificationResponse;
import com.blucharge.ocpp.enums.ConnectorState;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.service.ConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.blucharge.ocpp.constants.OcppConstants.TEST_CHARGER;

@Slf4j
@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private ChargerRepository chargerRepository;

@Override
    public StatusNotificationResponse statusNotification(StatusNotificationRequest parameters, String chargerIdentity){

        //DateTime timestamp = parameters.isSetTimestamp() ? parameters.getTimestamp() : DateTime.now();

    ChargerRecord charger =chargerRepository.getChargerFromChargerId(TEST_CHARGER);   //Exception handler that charger is null & charger doesnt exists in table



//    InsertConnectorStatusParams params =
//            InsertConnectorStatusParams.builder()
//                    .connectorId(parameters.getConnectorId())
//                    .status(parameters.getStatus())
//                    //.errorCode( parameters.getErrorCode()!=null ? parameters.getErrorCode() :null)
//                    .timestamp(timestamp)
//                    .errorInfo(parameters.getErrorInfo())
//                   // .vendorErrorCode(parameters.getVendorErrorCode())
//                    .build();

            Integer noOfConnectors = charger.getNoOfConnectors();

                if(parameters.getConnectorId() == 0){               // if it is 0; update heartbeat in Charger Table and move on
                    log.info("No of connectors for Charger Id {} , found to be 0 updating heartbeat", TEST_CHARGER);
                    chargerRepository.updateChargerHeartbeat(TEST_CHARGER, DateTime.now());
                    return new StatusNotificationResponse("Success");
                }
                ConnectorRecord connector = connectorRepository.getConnectorForChargerIdWithConnectorNumber(charger.getId(),parameters.getConnectorId());

                 if(Objects.isNull(connector))   //Exception handler if List empty
                 {
                     ConnectorRecord connectorRecord = new ConnectorRecord();
                     connectorRecord.setChargerId(charger.getId());
                     connectorRecord.setConnectorNumber(parameters.getConnectorId());
                     connectorRecord.setState(ConnectorState.IDLE.name()); // do we need this
                     connectorRepository.addConnector(connectorRecord);
                     chargerRepository.updateNumberOfConnectors(charger.getId(), noOfConnectors+1);
                 }
                    connectorRepository.updateConnectorStatus(parameters, charger.getId());
//            if(connectors.isEmpty()){
//                for(Integer i = 1; i <= noOfConnectors; i++)  {
//                    ConnectorRequest connectorRequest = new ConnectorRequest();
//                    connectorRequest.setChargerId(charger.getId());
//                    connectorRequest.setConnectorNumber(i);
//                    connectorRequest.setState(ConnectorStateStatus.IDLE.name());
//                    connectorRepository.insertConnector(connectorRequest);
//                    //connectorRepository.updateConnectorStatus(params,i, charger.getId());
//
//                }
//            }
//            else{
//                for(Integer i = 1; i <= noOfConnectors; i++){
//                    for(ConnectorRecord connectorRecord : connectors){
//                        if(connectorRecord.getConnectorNumber().equals(i) && connectorRecord.getChargerId().equals(charger.getId()))
//                            connectorRepository.updateConnectorStatus(params,i, charger.getId());
//                        else{
//                            ConnectorRequest connectorRequest = new ConnectorRequest();
//                            connectorRequest.setChargerId(charger.getId());
//                            connectorRequest.setConnectorNumber(i);
//                            connectorRequest.setState(ConnectorStateStatus.IDLE.name());
//                            connectorRepository.insertConnector(connectorRequest);
//                            connectorRepository.updateConnectorStatus(params,i, charger.getId());
//
//                        }
//
//                    }
//                }
//            }

 //       if((parameters.getStatus()== ChargerStatus.AVAILABLE.name())) {       //handles when connector exists in table but
//            Long connectorIndex = connectorRepository.getConnectorPkForChargeBoxAndConnector(charger.getId(),parameters.getConnectorId());
//            String connectorState = connectorRepository.getConnectorStateByConnectorPk(connectorIndex);
            //ConnectorRecord connectorRecord = new ConnectorRecord();
            //if(connectorState!=null &&(!ConnectorStateStatus.WAITING_FOR_CHARGER_RESPONSE.name().equals(connectorRecord.getStatus()) || DateTime.now().minusMinutes(10).isAfter(connectorRecord.getUpdatedOn())))

            //if(connectorState!=null &&(!ConnectorStateStatus.WAITING_FOR_CHARGER_RESPONSE.name().equals(connectorState) || DateTime.now().minusMinutes(10).isAfter(connectorRecord.getUpdatedOn())))
                //connectorRepository.updateConnectorStateToIdle(connectorIndex);
//        }
//        if (parameters.getStatus() == ChargerStatus.FAULTED.name()) {
//            log.error("Charger Status is Faulted!");
//        }

        return new StatusNotificationResponse("Success");
    }




}
