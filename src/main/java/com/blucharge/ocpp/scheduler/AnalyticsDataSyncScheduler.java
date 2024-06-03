package com.blucharge.ocpp.scheduler;

import com.blucharge.db.ocpp.tables.records.HubwiseChargerUptimeRecord;
import com.blucharge.db.ocpp.tables.records.LogHistoryRecord;
import com.blucharge.ocpp.config.JooqConfig;
import com.blucharge.ocpp.dto.Credentials;
import com.blucharge.ocpp.repository.HubWiseUpTimeRepo;
import com.blucharge.ocpp.repository.LogHistoryRepo;
import com.blucharge.ocpp.repository.LogHistoryTempRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

import java.util.List;


@Component
@DependsOn("jooqConfig")
@Slf4j
public class AnalyticsDataSyncScheduler {
    @Autowired
    private JooqConfig jooqConfig;
    @Autowired
    private LogHistoryTempRepo logHistoryTempRepo;
    @Autowired
    private LogHistoryRepo logHistoryRepo;
    @Autowired
    private HubWiseUpTimeRepo hubWiseUpTimeRepo;
    @Autowired
    private Credentials credentials;

    @Scheduled(cron = "0 0 0 * * ?")
    public void loadLogHisToryTempFromLogHistory() {
        List<LogHistoryRecord> logHistoryRecords = logHistoryRepo.getLastOneDayRecord(jooqConfig.dslOcppContext());
        for (LogHistoryRecord logHistoryRecord : logHistoryRecords) {
            logHistoryTempRepo.createRecord(logHistoryRecord, jooqConfig.dslOcppContext());
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void processLogHisToryTempToHubWiseUpTime() {
        Connection connection = null;
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

    @Scheduled(cron = "0 0 6 * * ?")
    public void writeDataToAnalytics() {
        List<HubwiseChargerUptimeRecord> hubwiseChargerUptimeRecords = hubWiseUpTimeRepo.getRecords(jooqConfig.dslOcppContext());
        for (HubwiseChargerUptimeRecord hubwiseChargerUptimeRecord : hubwiseChargerUptimeRecords) {
            hubWiseUpTimeRepo.createAnalyticsHubUpTimeRecord(hubwiseChargerUptimeRecord, jooqConfig.dslAnalyticsContext());
        }
        Connection connection = null;
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
}
