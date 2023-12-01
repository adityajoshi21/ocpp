package com.blucharge.ocpp.constants;

import com.blucharge.ocpp.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component("ApplicationConstants")
public class ApplicationConstants {
    public static String APPLICATION_ID;
    public static String KAFKA_URL;
    public static List<String> TRIGGER_MESSAGE;
    @Autowired
    private ConstantUtil constantUtil;

    @PostConstruct
    void init() throws IllegalAccessException {
        constantUtil.configurationOfConstantsFromDataBase(this);
    }
}

