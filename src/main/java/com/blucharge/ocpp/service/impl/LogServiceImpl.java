package com.blucharge.ocpp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.blucharge.core.exception.ResponseException;
import com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.config.JooqConfig;
import com.blucharge.ocpp.constants.ApplicationConstants;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private AmazonS3 s3Client;
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
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getLastOneDayRecordWithTimeStamp(jooqConfig.dslOcppContext(), logTempDataInsertRequestDto.getTimestamp());
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
    public void sendDataToS3(S3DataInsertRequestDto s3DataInsertRequestDto) {
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getRecordForStartAndEnd(s3DataInsertRequestDto.getStartTime(), s3DataInsertRequestDto.getEndTime(), jooqConfig.dslOcppContext());
        System.out.println("logHistoryRecords = " + logHistoryRecords.size());
        StringBuilder s = new StringBuilder();
        s.append("CHARGER_ID\tMESSAGE_TYPE\tMESSAGE_NAME\tMESSAGE_JSON\tCREATED_ON\n");
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            try {
                s.append(logHistoryRecord.getChargerId()).append("\t");
                s.append(logHistoryRecord.getMessageType()).append("\t");
                s.append(logHistoryRecord.getMessageName()).append("\t");
                s.append(logHistoryRecord.getMessageJson()).append("\t");
                s.append(logHistoryRecord.getCreatedOn()).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File file = createFile(s3DataInsertRequestDto.getName(), s.toString());
        uploadFile(file, s3DataInsertRequestDto.getName());
    }

    @Override
    public void deleteLogData(S3DataInsertRequestDto s3DataInsertRequestDto) {
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getRecordForStartAndEnd(s3DataInsertRequestDto.getStartTime(), s3DataInsertRequestDto.getEndTime(), jooqConfig.dslOcppContext());
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            logHistoryRepo.deleteRecord(logHistoryRecord.getId(), jooqConfig.dslOcppContext());
        }
    }

    private File createFile(String fileName, String data) {
        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void uploadFile(File tmpfile, String name) {
        try {
            if (tmpfile == null)
                throw new ResponseException("No file present.");
            try {
                if (!tmpfile.exists() && !tmpfile.createNewFile())
                    log.info("unable to create new file");
                s3Client.putObject(new PutObjectRequest(ApplicationConstants.AWS_S3_BUCKET_NAME, "ocpp/log" + "/" + name, tmpfile));
                Files.deleteIfExists(Paths.get(name));
            } catch (Exception e) {
                log.info("Error in saving file" + new Gson().toJson(e));
                throw e;
            } finally {
                if (tmpfile.exists() && tmpfile.delete())
                    log.info("temp file deleted successfully in final block" + name);
            }
        } catch (IOException e) {
            log.info("Error in storing file" + name + new Gson().toJson(e));
            throw new ResponseException("Failed to store file.");
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
