package com.blucharge.ocpp.config;

import com.blucharge.ocpp.dto.Credentials;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource(Credentials credentials) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(Integer.parseInt(credentials.getMysqlPoolSize()));
        config.setUsername(credentials.getMysqlUserName());
        config.setJdbcUrl("jdbc:mysql://" + credentials.getMysqlHostName() + ":" + credentials.getMysqlPort() + "/" + credentials.getMysqlDatabase() + "?serverTimezone=UTC&autoReconnect=true&useSSL=false");
        config.setPassword(credentials.getMysqlPassword());
        return new HikariDataSource(config);
    }
}
