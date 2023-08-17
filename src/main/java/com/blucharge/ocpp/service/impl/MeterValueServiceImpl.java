package com.blucharge.ocpp.service.impl;

import com.blucharge.ocpp.dto.ws.MeterValueRequest;
import com.blucharge.ocpp.dto.ws.MeterValueResponse;
import com.blucharge.ocpp.repository.MeterValueRepository;
import com.blucharge.ocpp.service.MeterValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MeterValueServiceImpl implements MeterValueService {

    @Autowired
    private MeterValueRepository meterValueRepository;

    @Override
    public MeterValueResponse meterValue(MeterValueRequest request, String chargerId) {
        if(request.isSetMeterValue()) {

            meterValueRepository.insertMeterValues(chargerId, request.getMeterValue(), request.getConnectorId(), request.getTransactionId());
            if(request.getTransactionId() != null){
                // get Transaction values from DB : To Do
               // transactionsRepository.getDetails(parameters.getConnectorId());
            }
        }
        return new MeterValueResponse();
    }
}
