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
        ChargerRecord chargerRecord = chargerRepository.getChargerFromChargerId(chargerId).get(0);
        if(request.isSetMeterValue()) {
            Long transactionId = request.getTransactionId();
            Integer connectorId = request.getConnectorId();
            if(! Objects.isNull(transactionId) && ! Objects.isNull(connectorId)) {
                TransactionRecord txnRecord = transactionsRepository.getActiveTransactionOnConnectorNoForTxnId(transactionId, connectorId);
                if (!Objects.isNull(txnRecord)) {
                    log.info("Saving meter values for Transaction ID : {}", request.getTransactionId());

                    meterValueRepository.insertMeterValues(chargerRecord.getId(), request.getMeterValue(), request.getConnectorId(), request.getTransactionId());
                }
            }
            else if (!Objects.isNull(connectorId)){
                TransactionRecord transactionRecord = transactionsRepository.getActiveTransactionOnConnectorId(connectorId, chargerRecord.getId());
                if (!Objects.isNull(transactionRecord)) {
                    log.info("Saving meter values for Connector ID : {}, Charger Id : {}", request.getConnectorId(), chargerRecord.getId());
                    meterValueRepository.insertMeterValues(chargerRecord.getId(), request.getMeterValue(), request.getConnectorId(), transactionRecord.getId());
                }
            }
            return new MeterValueResponse("{}");
        }
        else{
            log.info("Meter value not sent");
            return  new MeterValueResponse("Meter value were not sent in the request");
        }
    }
}