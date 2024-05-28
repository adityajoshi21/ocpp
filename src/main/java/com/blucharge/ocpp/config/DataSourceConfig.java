package com.blucharge.ocpp.config;

import com.blucharge.ocpp.dto.Credentials;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Slf4j
@Configuration("dataSourceConfig")
@DependsOn("secretsManager")
public class DataSourceConfig {
    @Autowired
    private SecretsManager secretsManager;

    @Bean
    public DataSource getOcppDataSource() {
        Credentials credentials = secretsManager.getSecret();
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(Integer.parseInt(credentials.getMysqlOcppPoolSize()));
        config.setUsername(credentials.getMysqlOcppUserName());
        config.setJdbcUrl("jdbc:mysql://" + credentials.getMysqlOcppHostName() + ":" + credentials.getMysqlOcppPort() + "/" + credentials.getMysqlOcppDatabase() + "?serverTimezone=UTC&autoReconnect=true&useSSL=false");
        config.setPassword(credentials.getMysqlOcppPassword());
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource getAnalyticsDataSource() {
        Credentials credentials = secretsManager.getSecret();
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(Integer.parseInt(credentials.getMysqlAnalyticsPoolSize()));
        config.setUsername(credentials.getMysqlAnalyticsUserName());
        config.setJdbcUrl("jdbc:mysql://" + credentials.getMysqlAnalyticsHostName() + ":" + credentials.getMysqlAnalyticsPort() + "/" + credentials.getMysqlAnalyticsDatabase() + "?serverTimezone=UTC&autoReconnect=true&useSSL=false");
        config.setPassword(credentials.getMysqlAnalyticsPassword());
        return new HikariDataSource(config);
    }
}
