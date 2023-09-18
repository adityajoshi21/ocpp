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
        if(transactionId == null){
            log.info("Transaction ID was returned NULL for ChargerId : {}", chargerIdentity);
            return;
        }
        if(transactionId == 0){
            log.info("Meter Value are being taken from the chargeBox, Txn Id found 0 for ChargerId; {}", chargerIdentity);
            return;
        }

        TransactionRecord transactionRecord= ctx.selectFrom(TRANSACTION).where(TRANSACTION.ID.eq(transactionId)).and(TRANSACTION.IS_ACTIVE.eq(true)).orderBy(TRANSACTION.ID.desc()).fetchAnyInto(TransactionRecord.class);

        if(transactionRecord ==null)
            return;

        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        Integer connectorNo = connectorRepository.getConnectorNoFromConnectorId(connectorId);
        Long connectorPk = connectorRepository.getConnectorPkForChargeBoxAndConnector(charger.getId(), connectorNo);

        if(transactionId != null){

            BigDecimal unit =null, startSoc=null, endSoc=null;

            log.info(" Transction Id : {} Meter Value list {}", transactionId,meterValues);
            for(MeterValue value : meterValues){
                startSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(startSoc);
                endSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && ! "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(endSoc);
                unit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(unit);
            }
            if (transactionRecord!=null && transactionRecord.getStartSoc() == null && startSoc != null) {  //For Setting Start SoC
                log.info("Setting start Soc for transaction ID : {} transaction Details Id : {}", transactionId, transactionRecord.getId());

                if(startSoc !=null){
                    transactionRecord.setStartSoc(startSoc);
                }
                update = true;
            }

            if (transactionRecord!=null && endSoc != null && transactionRecord.getStartSoc().compareTo(endSoc) < 0) {   //For cases when End SoC > Start SoC
                log.info("Setting end SoC for transaction ID :  {}", transactionId);
                transactionRecord.setEndSoc(endSoc);
                update = true;
            }
            if (transactionRecord!=null && unit != null) {                                     //For units consumed
                log.info("Setting end unit for transaction ID : {} ", transactionId);
                if (transactionRecord.getMeterStartValue() == null || transactionRecord.getMeterStartValue().doubleValue() < 0) {      //For falsy meter start values
                    transactionRecord.setMeterStartValue(unit);
                    ctx.update(TRANSACTION)
                            .set(TRANSACTION.METER_START_VALUE, unit)
                            .where(TRANSACTION.ID.equal(transactionRecord.getId()))
                            .execute();
                }
                transactionRecord.setMeterStopValue(unit);
                update = true;
            }
            if (update) {
                log.info("Update Details for transaction ID : {} ", transactionRecord.getId());
            }
            batchInsertMeterValues(ctx, meterValues, connectorPk,transactionId);
        }
    }


    @Override
    public void updateMeterValues(String chargeBoxIdentity, List<MeterValue> meterValues, Long transactionId) {             //used while stopping an ongoing transaction

        // Step 1 : Fetching connector from transaction table
        Long  connectorId = ctx.select(transaction.CONNECTOR_ID).from(transaction)
                .where(transaction.ID.equal(transactionId)).fetchOneInto(Long.class);

        TransactionRecord transactionRecord =  ctx.selectFrom(transaction)
                .where(transaction.ID.eq(transactionId))
                .and(transaction.IS_ACTIVE.eq(true))
                .and(transaction.CONNECTOR_ID.eq(connectorId))
                .orderBy(transaction.ID.desc())
                .fetchOneInto(TransactionRecord.class);


        BigDecimal soc = null;
        BigDecimal unit = null;

        for( MeterValue value : meterValues ) {
            soc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal :: compareTo).orElse(null);

            unit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(null);
        }
        if(transactionRecord.getStartSoc() == null && soc != null) {
            transactionRecord.setStartSoc(soc);
        }
        if(soc != null && transactionRecord.getStartSoc().compareTo(soc) < 0) {
            transactionRecord.setEndSoc(soc);
        }

        if(unit != null) {
            log.info("Setting end meter value for Transaction ID {}", transactionId);
            transactionRecord.setMeterStopValue(unit);

        }
        batchInsertMeterValues(ctx, meterValues, connectorId, transactionId);
    }

    @Override
    public void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, Long connectorPk, Long transactionId) {
        List<ConnectorMeterValueRecord> batch = new ArrayList<>();
        for(MeterValue mv : list){
            List<SampledValue> sampledValues = mv.getSampledValue();
            for(SampledValue sampledValue : sampledValues) {
                ConnectorMeterValueRecord connectorMeterValueRecord = ctx.newRecord(connectorMeterValue);
                connectorMeterValueRecord.setConnectorId(connectorPk);
                connectorMeterValueRecord.setTransactionId(transactionId);
                connectorMeterValueRecord.setValue(sampledValue.getValue());
                connectorMeterValueRecord.setSampledValueOn(mv.getTimestamp());

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
