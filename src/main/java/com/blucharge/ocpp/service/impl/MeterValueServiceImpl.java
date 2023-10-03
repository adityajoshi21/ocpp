package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.MeterValueRequest;
import com.blucharge.ocpp.dto.ws.MeterValueResponse;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
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
    @Autowired
    private ChargerRepository chargerRepository;
    @Autowired
    private ConnectorRepository connectorRepository;

    @Override
    public MeterValueResponse meterValue(MeterValueRequest request, String chargerId) {
        ChargerRecord chargerRecord = chargerRepository.getChargerFromChargerId(chargerId);
        if(request.isSetMeterValue()) {
            Long transactionId = request.getTransactionId();

            if(!Objects.isNull(transactionId)) {
                TransactionRecord txnRecord = transactionsRepository.getActiveTransactionOnConnectorNoForTxnId(transactionId, request.getConnectorId());
                if (!Objects.isNull(txnRecord)) {
                    log.info("Saving meter values for Transaction ID : {}", request.getTransactionId());

                    ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerId);
                    meterValueRepository.insertMeterValues(chargerRecord.getId(), request.getMeterValue(), request.getConnectorId(), request.getTransactionId());
                }
            }
        }
        return null;
    }
}