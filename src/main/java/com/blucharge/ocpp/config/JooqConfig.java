package com.blucharge.ocpp.config;

import com.blucharge.ocpp.config.intercepter.JooqAutoInsertUpdateListener;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultRecordListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;


@Configuration
public class JooqConfig {

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    public DefaultConfiguration configuration(DataSource dataSource) {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider(dataSource));
        jooqConfiguration.set(SQLDialect.MYSQL);
        jooqConfiguration.set(new Settings().withFetchWarnings(false));
        jooqConfiguration.set(new DefaultRecordListenerProvider(new JooqAutoInsertUpdateListener()));
        return jooqConfiguration;
    }

    @Bean
    public DefaultDSLContext dsl(DataSource dataSource) {
        return new DefaultDSLContext(configuration(dataSource));
    }

}
