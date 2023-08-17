package com.blucharge.ocpp.repository.impl;

import com.blucharge.db.ocpp.tables.ConnectorMeterValue;
import com.blucharge.db.ocpp.tables.Transaction;
import com.blucharge.db.ocpp.tables.records.ChargerRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorMeterValueRecord;
import com.blucharge.db.ocpp.tables.records.ConnectorRecord;
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
        if(transactionId == null){
            log.info("Transaction ID was returned NULL for ChargerId : {}", chargerIdentity);
            return ;
        }
        if(transactionId == 0){
            log.info("Meter Value are being taken from the chargeBox, Txn Id found 0 for ChargerId; {}", chargerIdentity);
            return;
        }

        TransactionRecord transaction = ctx.selectFrom(TRANSACTION).where(TRANSACTION.IS_ACTIVE).and(TRANSACTION.ID.eq(transactionId)).fetchAnyInto(TransactionRecord.class);
        if(transaction ==null)
            return;

        ChargerRecord charger = chargerRepository.getChargerFromChargerId(chargerIdentity);
        Integer connectorNo = connectorRepository.getConnectorNoFromConnectorId(connectorId);
        Long connectorPk = connectorRepository.getConnectorPkForChargeBoxAndConnector(charger.getId(), connectorNo);

        if(transactionId != null){
            TransactionRecord rec= ctx.selectFrom(TRANSACTION).where(TRANSACTION.ID.eq(transactionId)).and(TRANSACTION.IS_ACTIVE.eq(true)).orderBy(TRANSACTION.ID.desc()).fetchAnyInto(TransactionRecord.class);

            boolean update = false;
            BigDecimal unit =null, startSoc=null, endSoc=null, meterStopValue = null;

            log.info(" Transction Id : {} Meter Value list {}", transactionId,meterValues);
            for(MeterValue value : meterValues){
                startSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(startSoc);
                endSoc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!= null && x.getContext()!=null && ! "Transaction.Begin".equalsIgnoreCase(x.getContext().value())).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(endSoc);
                unit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null).map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(unit);
            }
            if (rec!=null && rec.getStartSoc() == null && startSoc != null) {  //For Start Soc
                log.info("Setting start Soc for transaction ID : {} transaction Details Id : {}", transactionId, rec.getId());

                rec.setStartSoc(startSoc);
                if(startSoc !=null){
                    rec.setStartSoc(startSoc);
                }
                update = true;
            }

            if (rec!=null && endSoc != null && rec.getStartSoc().compareTo(endSoc) < 0) {   //For End Soc
                log.info("Setting end SoC for transaction ID :  {}", transactionId);
                rec.setEndSoc(endSoc);
                update = true;
            }
            if (rec!=null && unit != null) {                                     //For units consumed
                log.info("Setting end unit for transaction ID : {} ", transactionId);
                if (rec.getMeterStartValue() == null || rec.getMeterStartValue().doubleValue() < 0) {
                    rec.setMeterStartValue(unit);
                    ctx.update(TRANSACTION)
                            .set(TRANSACTION.METER_START_VALUE, (unit))
                            .where(TRANSACTION.ID.equal(transactionId))
                            .execute();

                }
                rec.setMeterStopValue(unit);
                update = true;
            }

        }
        batchInsertMeterValues(ctx, meterValues, connectorPk,transactionId);
    }


    @Override
    public void insertMeterValues(String chargeBoxIdentity, List<MeterValue> meterValues, Long transactionId) {             //used while stopping an ongoing transaction

        // Step 1 : Fetching connector from transaction table
        Long  connectorPk = ctx.select(transaction.CONNECTOR_ID).from(transaction)
                .where(transaction.ID.equal(transactionId)).fetchOne().value1();

        TransactionRecord transactionRecord = (TransactionRecord) ctx.selectFrom(transaction).where(transaction.ID.eq(transactionId)).and(transaction.IS_ACTIVE.eq(true));

        boolean update = false;
        BigDecimal soc = null, unit = null;

        for( MeterValue value : meterValues ) {
            soc = value.getSampledValue().stream().filter( x -> "SoC".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal :: compareTo).orElse(null);

            unit = value.getSampledValue().stream().filter( x -> "Energy.Active.Import.Register".equalsIgnoreCase(x.getMeasurand().value()) && x.getValue()!=null)
                    .map(p -> new BigDecimal(p.getValue())).max(BigDecimal::compareTo).orElse(null);
        }
        if(transactionRecord.getStartSoc() == null && soc != null) {
            transactionRecord.setStartSoc(soc);
            update = true;
        }
        if(soc != null && transactionRecord.getStartSoc().compareTo(soc) < 0) {
            transactionRecord.setEndSoc(soc);
            update = true;
        }


        if(unit != null) {
            log.info("Setting end meter value for Transaction ID {}", transactionId);
            transactionRecord.setMeterStopValue(unit);
            update = true;
        }
    batchInsertMeterValues(ctx, meterValues, connectorPk, transactionId);
    }

    @Override
    public void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, Long connectorPk, Long transactionId) {
        List<ConnectorMeterValueRecord> batch = new ArrayList<>();
        for(MeterValue mv : list){
            List<SampledValue> sampledValues = mv.getSampledValue();
            for(SampledValue sampledValue : sampledValues) {
                ConnectorMeterValueRecord record = ctx.newRecord(connectorMeterValue);
                record.setConnectorId(connectorPk);
                record.setTransactionId(transactionId);
                record.setValue(sampledValue.getValue());
                record.setSampledValueOn(mv.getTimestamp());

                //Following are optional fields, so we put a conditional check if the values are set
                if (sampledValue.isSetContext()) {
                    record.setReadingContext(sampledValue.getContext().name());}

                if (sampledValue.isSetFormat()) {
                    record.setFormat(sampledValue.getFormat().name());}

                if (sampledValue.isSetMeasurand()) {
                    record.setMeasurand(sampledValue.getMeasurand().name());}

                if (sampledValue.isSetLocation()) {
                    record.setLocation(sampledValue.getLocation().name());}

                if (sampledValue.isSetUnit()) {
                    record.setUnit(sampledValue.getUnit());}
                if (sampledValue.isSetPhase()) {
                    record.setPhase(sampledValue.getPhase().value());}

                batch.add(record);
            }
        }
        ctx.batchInsert(batch).execute();

    }
}
