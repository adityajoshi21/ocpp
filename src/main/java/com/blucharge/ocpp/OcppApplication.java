package com.blucharge.ocpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.blucharge.core", "com.blucharge.ocpp", "com.blucharge.auth", "com.blucharge.event", "com.blucharge.util", "com.blucharge.exception"})
public class OcppApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcppApplication.class, args);
    }

}
