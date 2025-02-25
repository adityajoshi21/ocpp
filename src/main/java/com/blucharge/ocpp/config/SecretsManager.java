package com.blucharge.ocpp.config;

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


@Configuration("secretsManager")
@Slf4j
public class SecretsManager {


    @Bean
    public Credentials getSecret() {
        if ("local".equals(System.getenv("ENV"))) {
            return Credentials.builder()
                    .mysqlOcppDatabase("bluchargeOcppLocal")
                    .mysqlOcppHostName("localhost")
                    .mysqlOcppUserName("root")
                    .mysqlOcppPassword("root")
                    .mysqlOcppPort("3306")
                    .mysqlOcppPoolSize("10")
                    .mysqlAnalyticsDatabase("bluchargeAnalyticsLocal")
                    .mysqlAnalyticsHostName("localhost")
                    .mysqlAnalyticsUserName("root")
                    .mysqlAnalyticsPassword("root")
                    .mysqlAnalyticsPort("3306")
                    .mysqlAnalyticsPoolSize("10")
                    .build();
        }
        log.info("Env : {}", System.getenv("ENV"));
        String secretName = System.getenv("ENV") + "/ocpp/mysql";
        Region region = Region.of("ap-south-1");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        String secret = null, decodedBinarySecret = null;
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
        log.info("Secrets fetched: " + new Gson().toJson(secret));
        return new Gson().fromJson(secret, Credentials.class);

    }

}
