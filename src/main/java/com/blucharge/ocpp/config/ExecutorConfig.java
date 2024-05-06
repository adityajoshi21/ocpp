package com.blucharge.ocpp.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableAsync
public class ExecutorConfig {
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("AsyncThread--");
        executor.initialize();
        return executor;
    }


    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("SteVe-Executor-%d")
                .build();

        return new ScheduledThreadPoolExecutor(30, threadFactory);
    }

}
