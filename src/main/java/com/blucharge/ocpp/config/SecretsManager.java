package com.blucharge.ocpp.config;


//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;

import com.blucharge.ocpp.dto.Credentials;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

import java.security.InvalidParameterException;
import java.util.Base64;


@Configuration
@Slf4j
public class SecretsManager {


    @Bean
    public Credentials getSecret() {
        if("local".equals(System.getenv("ENV"))){
            return Credentials.builder().mysqlDatabase("bluchargeOcppLocal").mysqlHostName("localhost").mysqlUserName("root").mysqlPassword("root").mysqlPort("3306").mysqlPoolSize("10").build();
        }
        log.info("Env : {}",System.getenv("ENV"));
        String secretName = "ocpp/"+System.getenv("ENV")+"/mysql";
        Region region = Region.of("ap-south-1");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        String secret=null, decodedBinarySecret = null;
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse getSecretValueResponse = null;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException | InternalServiceErrorException |
                 InvalidParameterException | InvalidRequestException | ResourceNotFoundException e) {
            throw e;
        }

        if (getSecretValueResponse.secretString() != null) {
            secret = getSecretValueResponse.secretString();
        } else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResponse.secretBinary().asByteBuffer()).array());
        }

        return  new Gson().fromJson(secret,Credentials.class);

    }




}
