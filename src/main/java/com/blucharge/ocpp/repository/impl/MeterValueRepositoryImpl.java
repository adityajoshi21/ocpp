package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.ConnectorMeterValue;
import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorMeterValueRecord;
import com.blucharge.db.ocpp.tables.records.TransactionRecord;
import com.blucharge.ocpp.dto.ws.MeterValue;
import com.blucharge.ocpp.dto.ws.SampledValue;
import com.blucharge.ocpp.repository.ChargerRepository;
import com.blucharge.ocpp.repository.ConnectorRepository;
import com.blucharge.ocpp.repository.MeterValueRepository;
import com.blucharge.ocpp.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.blucharge.db.ocpp.tables.Transaction.TRANSACTION;

@Slf4j
@Repository
public class MeterValueRepositoryImpl implements MeterValueRepository {
    @Autowired
    private DSLContext ctx;

    private static final ConnectorMeterValue connectorMeterValue = ConnectorMeterValue.CONNECTOR_METER_VALUE;
    private static final Transaction transaction = TRANSACTION;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private ChargerRepository chargerRepository;
    @Override
    public void insertMeterValues(String chargerIdentity, List<MeterValue> meterValues, Long connectorId, Long transactionId) {

        log.info("Saving meter values for Transaction ID : {}", transactionId);
        Boolean update = false;
        if(Objects.isNull(transactionId)){
            log.info("Transaction ID was returned NULL for ChargerId : {}", chargerIdentity);
            return;
        }
        if(transactionId == 0){
            log.info("Meter Value are being taken from the chargeBox, Txn Id found 0 for ChargerId; {}", chargerIdentity);
            return;
        }

        TransactionRecord transactionRecord= ctx.selectFrom(TRANSACTION).where(TRANSACTION.ID.eq(transactionId)).and(TRANSACTION.IS_ACTIVE.eq(true)).orderBy(TRANSACTION.ID.desc()).fetchOneInto(TransactionRecord.class);

        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        Integer connectorNo = connectorRepository.getConnectorNoFromConnectorId(connectorId);
        Long connectorPk = connectorRepository.getConnectorPkForChargeBoxAndConnector(charger.getId(), connectorNo);

        BigDecimal startUnit =null, startSoc=null, endSoc=null;

        log.info(" Transaction Id : {} Meter Value list {}", transactionId,meterValues);
        for(MeterValue value : meterValues){
            startSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(startSoc);
            endSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && ! "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(endSoc);
            startUnit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(startUnit);
        }
        if (transactionRecord!=null && transactionRecord.getStartSoc() == null && startSoc != null) {                       //For Setting Start SoC
            log.info("Setting start Soc : {} for transaction Id : {}",startSoc, transactionId);

            transactionRecord.setStartSoc(startSoc);
            transactionRecord.update();
            update = true;
        }

        if (transactionRecord!=null && endSoc != null && transactionRecord.getStartSoc().compareTo(endSoc) < 0) {   //Check for cases when End SoC > Start SoC
            log.info("Setting end SoC {} for transaction ID :  {}", endSoc, transactionId);
            transactionRecord.setEndSoc(endSoc);
            transactionRecord.update();
            update = true;
        }
        if (transactionRecord!=null && startUnit != null) {      //For units consumed
            log.info("Setting end unit for transaction ID : {} ", transactionId);
            if (transactionRecord.getMeterStartValue() == null || transactionRecord.getMeterStartValue().doubleValue() < 0) {      //For existing falsy meter start value readings
                transactionRecord.setMeterStartValue(startUnit);
                transactionRecord.update();
            }
            update = true;
        }
        if (update) {
            log.info("Update Details for transaction ID : {} ", transactionRecord.getId());
            batchInsertMeterValues(ctx, meterValues, connectorPk,transactionId);
        }

    }


    @Override
    public void updateMeterValues(String chargeBoxIdentity, List<MeterValue> meterValues, Long transactionId) {             //used while stopping an ongoing transaction

        // Step 1 : Fetching connector on which meter value is to be updated in transaction table
        Long  connectorId = ctx.select(transaction.CONNECTOR_ID).from(transaction)
                .where(transaction.ID.equal(transactionId)).fetchOneInto(Long.class);

        //Step 2 : Fetching transaction for which meter value is to be updated
        TransactionRecord transactionRecord =  ctx.selectFrom(transaction)
                .where(transaction.ID.eq(transactionId))
                .and(transaction.IS_ACTIVE.eq(true))
                .and(transaction.CONNECTOR_ID.eq(connectorId))
                .orderBy(transaction.ID.desc())
                .fetchOneInto(TransactionRecord.class);

        BigDecimal endSoc = null;
        BigDecimal stopUnit = null;

        for( MeterValue value : meterValues ) {
            endSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal :: compareTo).orElse(null);

            stopUnit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(null);
        }
        if(transactionRecord.getStartSoc() == null && endSoc != null) {
            transactionRecord.setStartSoc(endSoc);      //For cases when there is a power loss
            transactionRecord.update();
        }
        if(endSoc != null && transactionRecord.getStartSoc().compareTo(endSoc) < 0) {       //Just a check to confirm that end soc > start soc
            transactionRecord.setEndSoc(endSoc);
            transactionRecord.update();
        }

        if(stopUnit != null) {      //For updating power related value for ongoing transaction
            log.info("Setting end meter value for Transaction ID {}", transactionId);
            transactionRecord.setMeterStopValue(stopUnit);
            transactionRecord.update();

        }
        batchInsertMeterValues(ctx, meterValues, connectorId, transactionId);
    }

    @Override
    public void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, Long connectorPk, Long transactionId) {
        List<ConnectorMeterValueRecord> batch = new ArrayList<>();
        for(MeterValue meterValue : list){
            List<SampledValue> sampledValues = meterValue.getSampledValue();
            for(SampledValue sampledValue : sampledValues) {
                ConnectorMeterValueRecord connectorMeterValueRecord = ctx.newRecord(connectorMeterValue);
                connectorMeterValueRecord.setConnectorId(connectorPk);
                connectorMeterValueRecord.setTransactionId(transactionId);
                connectorMeterValueRecord.setValue(sampledValue.getValue());
                connectorMeterValueRecord.setSampledValueOn(meterValue.getTimestamp());

                //Following are optional fields, so we put a conditional check if the values are set
                if (sampledValue.isSetContext()) {
                    connectorMeterValueRecord.setReadingContext(sampledValue.getContext().name());}

                if (sampledValue.isSetFormat()) {
                    connectorMeterValueRecord.setFormat(sampledValue.getFormat().name());}

                if (sampledValue.isSetMeasurand()) {
                    connectorMeterValueRecord.setMeasurand(sampledValue.getMeasurand().name());}

                if (sampledValue.isSetLocation()) {
                    connectorMeterValueRecord.setLocation(sampledValue.getLocation().name());}

                if (sampledValue.isSetUnit()) {
                    connectorMeterValueRecord.setUnit(sampledValue.getUnit());}
                if (sampledValue.isSetPhase()) {
                    connectorMeterValueRecord.setPhase(sampledValue.getPhase().value());}

                batch.add(connectorMeterValueRecord);
            }
        }
        ctx.batchInsert(batch).execute();

    }
}
