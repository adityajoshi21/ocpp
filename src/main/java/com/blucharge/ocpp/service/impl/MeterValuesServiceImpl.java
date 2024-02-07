package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargingTransactionHistoryRecord;
import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;
import com.blucharge.event.dto.ChargingTxnUpdateEventDto;
import com.blucharge.event.dto.KafkaPublishEventDto;
import com.blucharge.event.enums.ConnectorEvent;
import com.blucharge.event.enums.KafkaEventType;
import com.blucharge.event.enums.KafkaTopic;
import com.blucharge.ocpp.config.KafkaConfiguration;
import com.blucharge.ocpp.constants.ApplicationConstants;
import com.blucharge.ocpp.dto.MeterValue;
import com.blucharge.ocpp.dto.SampledValue;
import com.blucharge.ocpp.dto.meter_value.MeterValuesRequest;
import com.blucharge.ocpp.dto.meter_value.MeterValuesResponse;
import com.blucharge.ocpp.enums.Measurand;
import com.blucharge.ocpp.repository.ChargingTransactionHistoryRepo;
import com.blucharge.ocpp.repository.EventRepo;
import com.blucharge.ocpp.repository.LiveTransactionRepo;
import com.blucharge.ocpp.repository.MeterValuesRepo;
import com.blucharge.ocpp.service.MeterValuesService;
import com.blucharge.util.utils.RequestContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MeterValuesServiceImpl implements MeterValuesService {
    @Autowired
    private MeterValuesRepo meterValueRepository;
    @Autowired
    private LiveTransactionRepo liveTransactionRepo;
    @Autowired
    private ChargingTransactionHistoryRepo chargingTransactionHistoryRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Override
    public MeterValuesResponse insertMeterValue(MeterValuesRequest request, String chargerId) {
        String socMeasurand = getMeasurandValueFromMeterValue(request.getMeterValue(), Measurand.SOC);
        String energyImported = getMeasurandValueFromMeterValue(request.getMeterValue(), Measurand.ENERGY_ACTIVE_IMPORT_REGISTER);
        ChargingTransactionHistoryRecord chargingTransactionHistoryRecord = chargingTransactionHistoryRepo.getChargingTxnHistoryRecordForId(request.getTransactionId().longValue());
        if (Objects.isNull(chargingTransactionHistoryRecord.getStartSoc())) {
            chargingTransactionHistoryRepo.updateTransactionForStartSoc(chargingTransactionHistoryRecord.getId(), socMeasurand);
        }
        liveTransactionRepo.updateTransactionForMeterValue(
                request.getTransactionId().longValue(),
                socMeasurand,
                energyImported,
                Objects.isNull(chargingTransactionHistoryRecord.getStartSoc())
        );
        LiveTransactionRecord liveTransactionRecord = liveTransactionRepo.getLiveTransactionRecordForTxnId(request.getTransactionId().longValue());
        for (MeterValue meterValue : request.getMeterValue()) {
            for (SampledValue sampledValue : meterValue.getSampledValue()) {
                meterValueRepository.insertSampledValue(liveTransactionRecord, sampledValue, meterValue.getTimestamp());
            }
        }
        // Info: kafka charging txn update event
        KafkaPublishEventDto<ChargingTxnUpdateEventDto> eventDto = new KafkaPublishEventDto<>();
        eventDto.setTopic(KafkaTopic.CONNECTOR.name());
        eventDto.setEventType(KafkaEventType.REQUEST.name());
        eventDto.setEventName(ConnectorEvent.CHARGING_UPDATE.name());
        eventDto.setApplicationSourceId(ApplicationConstants.APPLICATION_ID);
        eventDto.setOrganisationId(RequestContext.getOrganizationId());
        eventDto.setCreatedBy("OCPP");
        eventDto.setEventData(new ChargingTxnUpdateEventDto(
                        liveTransactionRecord.getUuid(),
                        liveTransactionRecord.getCurrentSoc(),
                        liveTransactionRecord.getUnitConsumed(),
                        null
                )
        );
        eventRepo.createRecordFromEvent(eventDto);
        kafkaConfiguration.kafkaTemplate().send(eventDto.getTopic(), new Gson().toJson(eventDto));

        return new MeterValuesResponse();
    }

    private String getMeasurandValueFromMeterValue(List<MeterValue> meterValueList, Measurand measurand) {
        for (MeterValue meterValue : meterValueList) {
            if (meterValue.getSampledValue().size() > 0) {
                List<SampledValue> sampledValueList = meterValue.getSampledValue();
                for (SampledValue sampledValue : sampledValueList) {
                    if (measurand.equals(sampledValue.getMeasurand())) {
                        return sampledValue.getValue();
                    }
                }
            }
        }
        return null;
    }
}