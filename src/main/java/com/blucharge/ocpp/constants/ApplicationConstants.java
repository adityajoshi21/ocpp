package com.blucharge.ocpp.constants;

import com.blucharge.ocpp.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("ApplicationConstants")
@DependsOn("constantUtil")
public class ApplicationConstants {
    public static String APPLICATION_ID;
    public static String AWS_ACCESS_KEY_ID;
    public static String AWS_ACCESS_KEY_SECRET;
    public static String AWS_S3_BUCKET_NAME;
    @Autowired
    private ConstantUtil constantUtil;

    @PostConstruct
    void init() throws IllegalAccessException {
        constantUtil.configurationOfConstantsFromDataBase(this);
    }
}

