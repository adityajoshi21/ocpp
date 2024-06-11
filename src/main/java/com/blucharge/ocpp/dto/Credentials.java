package com.blucharge.ocpp.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {
    @SerializedName("ocpp-hostname")
    private String mysqlOcppHostName;
    @SerializedName("ocpp-port")
    private String mysqlOcppPort;
    @SerializedName("ocpp-username")
    private String mysqlOcppUserName;
    @SerializedName("ocpp-password")
    private String mysqlOcppPassword;
    @SerializedName("ocpp-database")
    private String mysqlOcppDatabase;
    @SerializedName("pool-size-ocpp")
    private String mysqlOcppPoolSize;
    @SerializedName("analytics-hostname")
    private String mysqlAnalyticsHostName;
    @SerializedName("analytics-port")
    private String mysqlAnalyticsPort;
    @SerializedName("analytics-username")
    private String mysqlAnalyticsUserName;
    @SerializedName("analytics-password")
    private String mysqlAnalyticsPassword;
    @SerializedName("analytics-database")
    private String mysqlAnalyticsDatabase;
    @SerializedName("pool-size-analytics")
    private String mysqlAnalyticsPoolSize;
}
