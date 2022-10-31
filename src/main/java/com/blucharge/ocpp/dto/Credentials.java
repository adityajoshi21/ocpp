package com.blucharge.ocpp.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Credentials {
    @SerializedName("hostname")
    private String mysqlHostName;
    @SerializedName("port")
    private String mysqlPort;
    @SerializedName("username")
    private String mysqlUserName;
    @SerializedName("password")
    private String mysqlPassword;
    @SerializedName("database")
    private String mysqlDatabase;
    @SerializedName("pool-size")
    private String mysqlPoolSize;
}
