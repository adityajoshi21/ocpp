package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.LiveTransactionRecord;
import com.blucharge.ocpp.dto.SampledValue;
import com.blucharge.ocpp.dto.meter_value.MeterValue;
import com.blucharge.ocpp.dto.meter_value.MeterValuesRequest;
import com.blucharge.ocpp.dto.meter_value.MeterValuesResponse;
import com.blucharge.ocpp.enums.Measurand;
import com.blucharge.ocpp.repository.LiveTransactionRepo;
import com.blucharge.ocpp.repository.MeterValuesRepository;
import com.blucharge.ocpp.service.MeterValuesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MeterValuesServiceImpl implements MeterValuesService {
    @Autowired
    private MeterValuesRepository meterValueRepository;
    @Autowired
    private LiveTransactionRepo liveTransactionRepo;

    @Override
    public MeterValuesResponse insertMeterValue(MeterValuesRequest request, String chargerId) {
        String socMeasurand = getMeasurandValueFromMeterValue(request.getMeterValue(), Measurand.SOC);
        String energyImported = getMeasurandValueFromMeterValue(request.getMeterValue(), Measurand.ENERGY_ACTIVE_IMPORT_REGISTER);
        liveTransactionRepo.updateTransactionForMeterValue(request.getTransactionId().longValue(), socMeasurand, energyImported);
        LiveTransactionRecord liveTransactionRecord = liveTransactionRepo.getLiveTransactionRecordForTxnId(request.getTransactionId().longValue());
        for (MeterValue meterValue : request.getMeterValue()) {
            for (SampledValue sampledValue : meterValue.getSampledValue()) {
                meterValueRepository.insertSampledValue(liveTransactionRecord, sampledValue, meterValue.getTimestamp());
            }
        }
        return new MeterValuesResponse("ACCEPTED");
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