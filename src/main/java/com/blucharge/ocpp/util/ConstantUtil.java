package com.blucharge.ocpp.util;


import com.blucharge.core.enums.PropertyTypes;
import com.blucharge.db.ocpp.tables.records.ApplicationPropertiesRecord;
import com.blucharge.ocpp.config.JooqConfig;
import com.blucharge.ocpp.repository.ApplicationPropertiesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("constantUtil")
@DependsOn("jooqConfig")
public class ConstantUtil {
    @Autowired
    private ApplicationPropertiesRepo applicationPropertiesRepo;
    @Autowired
    private JooqConfig jooqConfig;

    public void configurationOfConstantsFromDataBase(Object app) throws IllegalAccessException {
        Map<String, ApplicationPropertiesRecord> configMap = applicationPropertiesRepo.getPropertyMap(jooqConfig.dslOcppContext());
        if (configMap == null) return;
        Field[] fields = app.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (configMap.containsKey(field.getName()) && (!StringUtils.isEmpty(configMap.get(field.getName()).getPropertyValue()))) {
                log.info("In [configurationOfConstantsFromDataBase] applicationConstantsName: {} - constantValue: {} ", field.getName(), configMap.get(field.getName()).getPropertyValue());
                switch (PropertyTypes.valueOf(configMap.get(field.getName()).getPropertyType())) {
                    case INT:
                        field.set(app, Integer.parseInt(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case FLOAT:
                        field.set(app, Float.valueOf(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case LIST:
                        field.set(app, Arrays.asList(configMap.get(field.getName()).getPropertyValue().split(",")).parallelStream().map(String::trim).collect(Collectors.toList()));
                        break;
                    case MAP:
                        break;
                    case INTEGERLIST:
                        field.set(app, Arrays.stream(configMap.get(field.getName()).getPropertyValue().split(",")).map(String::trim).map(Integer::valueOf).collect(Collectors.toList()));
                        break;
                    case LONG:
                        field.set(app, Long.valueOf(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case STRING:
                        field.set(app, configMap.get(field.getName()).getPropertyValue());
                        break;
                    case BOOL:
                        field.set(app, Boolean.valueOf(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case BIGDECIMAL:
                        field.set(app, new BigDecimal(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case DOUBLE:
                        field.set(app, Double.valueOf(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    case INTEGER:
                        field.set(app, Integer.valueOf(configMap.get(field.getName()).getPropertyValue()));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + PropertyTypes.valueOf(configMap.get(field.getName()).getPropertyType()));
                }
            }
        }
    }
}