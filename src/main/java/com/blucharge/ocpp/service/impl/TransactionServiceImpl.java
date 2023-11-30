package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.*;
import com.blucharge.event.dto.RemoteStartCommandDto;
import com.blucharge.event.dto.RemoteStopCommandDto;
import com.blucharge.ocpp.dto.IdTagInfo;
import com.blucharge.ocpp.dto.IdToken;
import com.blucharge.ocpp.dto.authorize.AuthorizeRequest;
import com.blucharge.ocpp.dto.meter_value.MeterValuesRequest;
import com.blucharge.ocpp.dto.remote_start.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.remote_stop.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionRequest;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionResponse;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionRequest;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionResponse;
import com.blucharge.ocpp.enums.AuthorizationStatus;
import com.blucharge.ocpp.repository.*;
import com.blucharge.ocpp.service.MeterValuesService;
import com.blucharge.ocpp.service.OcppTagService;
import com.blucharge.ocpp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private ChargingTransactionHistoryRepo chargingTransactionHistoryRepo;
    @Autowired
    private OcppTagService ocppTagService;
    @Autowired
    private ChargerRepo chargerRepo;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private OcppTagRepository ocppTagRepository;
    @Autowired
    private LiveTransactionRepo liveTransactionRepo;
    @Autowired
    private MeterValuesService meterValuesService;

    @Override
    public StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerName) {
        IdTagInfo idTagInfo = ocppTagService.checkUserAuth(
                new AuthorizeRequest(
                        request.getIdTag()
                ),
                chargerName
        ).getIdTagInfo();

        if (!AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())) {
            return new StartTransactionResponse(
                    null,
                    idTagInfo
            );
        }

        ChargerRecord chargerRecord = chargerRepo.getChargerRecordFromName(chargerName);
        ConnectorRecord connectorRecord = connectorRepository.getConnectorRecordForChargerIdAndConnectorNumber(chargerRecord.getId(), request.getConnectorId());
        OcppTagRecord ocppTagRecord = ocppTagRepository.getOcppTagRecordForIdTag(request.getIdTag().getIdToken());
        ChargingTransactionHistoryRecord chargingTransactionHistoryRecord = new ChargingTransactionHistoryRecord();
        chargingTransactionHistoryRecord.setOcppTagId(ocppTagRecord.getId());
        chargingTransactionHistoryRecord.setChargerId(chargerRecord.getId());
        chargingTransactionHistoryRecord.setConnectorId(connectorRecord.getId());
        chargingTransactionHistoryRecord.setMeterStartValue(request.getMeterStartValue());
        chargingTransactionHistoryRecord.setStartTime(request.getTimestamp().getMillis());
        chargingTransactionHistoryRecord.setConnectorId(connectorRecord.getId());
        chargingTransactionHistoryRecord.setStatus("PENDING");
        ChargingTransactionHistoryRecord chargingTransactionHistoryRecord1 = chargingTransactionHistoryRepo.createRecord(chargingTransactionHistoryRecord);
        LiveTransactionRecord liveTransactionRecord = new LiveTransactionRecord();
        liveTransactionRecord.setConnectorId(chargingTransactionHistoryRecord1.getConnectorId());
        liveTransactionRecord.setChargingTransactionHistoryId(chargingTransactionHistoryRecord1.getId());
        liveTransactionRecord.setOcppTagId(chargingTransactionHistoryRecord1.getOcppTagId());
        liveTransactionRecord.setStartSoc(chargingTransactionHistoryRecord1.getStartSoc());
        liveTransactionRecord.setStartTime(chargingTransactionHistoryRecord1.getStartTime());
        liveTransactionRecord.setChargerId(chargingTransactionHistoryRecord1.getChargerId());
        liveTransactionRecord.setMeterStartValue(chargingTransactionHistoryRecord1.getMeterStartValue());
        liveTransactionRecord.setUnitConsumed(0.0);
        liveTransactionRepo.createRecord(liveTransactionRecord);
        return new StartTransactionResponse(
                chargingTransactionHistoryRecord1.getId().intValue(),
                idTagInfo
        );
    }

    @Override
    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargerName) {
        if (Objects.nonNull(parameters.getTransactionData()) && parameters.getTransactionData().size() > 0) {
            meterValuesService.insertMeterValue(
                    new MeterValuesRequest(
                            null,
                            parameters.getTransactionId(),
                            parameters.getTransactionData()
                    ), chargerName
            );
        }
        LiveTransactionRecord liveTransactionRecord = liveTransactionRepo.getLiveTransactionRecordForTxnId(parameters.getTransactionId());
        chargingTransactionHistoryRepo.updateTransactionForStopTransaction(
                parameters.getTransactionId(),
                parameters.getMeterStopValue(),
                parameters.getTimestamp(),
                parameters.getReason(),
                liveTransactionRecord.getStartTime(),
                liveTransactionRecord.getMeterStartValue()
        );
        liveTransactionRepo.deleteRecord(liveTransactionRecord.getId());
        return new StopTransactionResponse();
    }

    @Override
    public void handleRemoteStopCommand(RemoteStopCommandDto remoteStopCommandDto) {
        ChargingTransactionHistoryRecord chargingTransactionHistoryRecord = chargingTransactionHistoryRepo.getChargingTxnHistoryRecordForUuid(remoteStopCommandDto.getTxnId());
        // todo should we put check that same user should sent remote stop
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(chargingTransactionHistoryRecord.getChargerId());
        RemoteStopTransactionRequest remoteStopTransactionRequest = new RemoteStopTransactionRequest();
        remoteStopTransactionRequest.setTransactionId(chargingTransactionHistoryRecord.getId().intValue());
        String chargerName = chargerRecord.getName();
        // todo socket publish with above details
    }

    @Override
    public void handleRemoteStartCommand(RemoteStartCommandDto remoteStartCommandDto) {
        OcppTagRecord ocppTagRecord = ocppTagRepository.getOcppTagForCustomer(remoteStartCommandDto.getCustomerId());
        ConnectorRecord connectorRecord = connectorRepository.getConnectorFromUuid(remoteStartCommandDto.getConnectorId());
        ChargerRecord chargerRecord = chargerRepo.getChargerRecordForId(connectorRecord.getChargerId());
        RemoteStartTransactionRequest remoteStartTransactionRequest = new RemoteStartTransactionRequest();
        remoteStartTransactionRequest.setConnectorId(connectorRecord.getConnectorNumber());
        remoteStartTransactionRequest.setIdTag(
                new IdToken(
                        ocppTagRecord.getIdTag()
                )
        );
        String chargerName = chargerRecord.getName();
        // todo socket publish with above details
    }

}