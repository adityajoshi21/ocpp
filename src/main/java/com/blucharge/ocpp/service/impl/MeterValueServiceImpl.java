package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.MeterValueRequest;
import com.blucharge.ocpp.dto.ws.MeterValueResponse;
import com.blucharge.ocpp.repository.MeterValueRepository;
import com.blucharge.ocpp.repository.TransactionsRepository;
import com.blucharge.ocpp.service.MeterValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class MeterValueServiceImpl implements MeterValueService {

    @Autowired
    private MeterValueRepository meterValueRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Override
    public MeterValueResponse meterValue(MeterValueRequest request, String chargerId) {
        if(request.isSetMeterValue()) {
            TransactionRecord rec = transactionsRepository.getActiveTransctionForTxnId(request.getTransactionId());
            if(Objects.nonNull(rec)){
                meterValueRepository.insertMeterValues(chargerId, request.getMeterValue(), request.getConnectorId(), request.getTransactionId());
            }
        }
        return new MeterValueResponse();
    }
}