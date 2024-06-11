package com.blucharge.ocpp.service.impl;

import com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.config.JooqConfig;
import com.blucharge.ocpp.dto.*;
import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;
import com.blucharge.ocpp.repository.HubWiseUpTimeRepo;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import com.blucharge.ocpp.repository.LogHistoryTempRepo;
import com.blucharge.ocpp.service.LogService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private LogHistoryRepo logHistoryRepo;
    @Autowired
    private JooqConfig jooqConfig;
    @Autowired
    private Credentials credentials;
    @Autowired
    private LogHistoryTempRepo logHistoryTempRepo;
    @Autowired
    private HubWiseUpTimeRepo hubWiseUpTimeRepo;

    @Override
    public void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto) {
        LogHistoryRecord logHistoryRecord = new LogHistoryRecord();
        logHistoryRecord.setMessageType("RESPONSE");
        logHistoryRecord.setMessageName(ocppSocketDataFromBlucgnDto.getInputType());
        logHistoryRecord.setMessageJson(ocppSocketDataFromBlucgnDto.getInputData());
        logHistoryRecord.setChargerId(ocppSocketDataFromBlucgnDto.getChargeId());
        logHistoryRecord.setMessageOrigin("BLUCGN");
        logHistoryRepo.addRecord(logHistoryRecord, jooqConfig.dslOcppContext());
    }

    @Override
    public List<OcppLogResponseData> generateLogForChargeBox(OcppLogRequestBody ocppLogRequestBody) {
        List<OcppLogResponseData> response = new ArrayList<>();
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getRecord(ocppLogRequestBody, jooqConfig.dslOcppContext());
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            if (logHistoryRecord.getCreatedOn().getMillis() >= ocppLogRequestBody.getStartTime() && logHistoryRecord.getCreatedOn().getMillis() <= ocppLogRequestBody.getEndTime()) {
                OcppLogResponseData ocppLogResponseData = new OcppLogResponseData();
                ocppLogResponseData.setId(logHistoryRecord.getId());
                ocppLogResponseData.setChargeBoxId(logHistoryRecord.getChargerId());
                ocppLogResponseData.setMessageName(logHistoryRecord.getMessageName());
                ocppLogResponseData.setCreatedOn(logHistoryRecord.getCreatedOn().getMillis());
                OcppLogData ocppLogData = new Gson().fromJson(logHistoryRecord.getMessageJson(), OcppLogData.class);
                ocppLogResponseData.setDataJson(new Gson().toJson(ocppLogData));
                response.add(ocppLogResponseData);
            }
        }
        return response;
    }

    @Override
    public void insertDataInTempTable(LogTempDataInsertRequestDto logTempDataInsertRequestDto) {
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getLastOneDayRecordWithTimeStamp(jooqConfig.dslOcppContext(),logTempDataInsertRequestDto.getTimestamp());
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            logHistoryTempRepo.createRecord(logHistoryRecord, jooqConfig.dslOcppContext());
        }
    }

    @Override
    public void sendDataToHubWiseAnalytics() {
        List<HubwiseChargerUptimeRecord> hubwiseChargerUptimeRecords = hubWiseUpTimeRepo.getRecords(jooqConfig.dslOcppContext());
        for (HubwiseChargerUptimeRecord hubwiseChargerUptimeRecord : hubwiseChargerUptimeRecords) {
            hubWiseUpTimeRepo.createAnalyticsHubUpTimeRecord(hubwiseChargerUptimeRecord, jooqConfig.dslAnalyticsContext());
        }
        Connection connection;
        CallableStatement callableStatement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + credentials.getMysqlOcppHostName() + ":" + credentials.getMysqlOcppPort() + "/" + credentials.getMysqlOcppDatabase() + "?serverTimezone=UTC&autoReconnect=true&useSSL=false"
                    , credentials.getMysqlOcppUserName(), credentials.getMysqlOcppPassword());

            String sql = "{call delete_charger_uptime()}";
            callableStatement = connection.prepareCall(sql);
            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void processLogHisToryTempToHubWiseUpTime() {
        Connection connection;
        CallableStatement callableStatement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + credentials.getMysqlOcppHostName() + ":" + credentials.getMysqlOcppPort() + "/" + credentials.getMysqlOcppDatabase() + "?serverTimezone=UTC&autoReconnect=true&useSSL=false"
                    , credentials.getMysqlOcppUserName(), credentials.getMysqlOcppPassword());

            String sql = "{call hubwise_charger_uptime_proc()}";
            callableStatement = connection.prepareCall(sql);
            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
