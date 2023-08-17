package com.blucharge.ocpp.service.impl;//package com.blucharge.ocpp.service.impl;
//
//import com.blucharge.ocpp.dto.UnidentifiedIncomingObject;
//import com.blucharge.ocpp.service.UnidentifiedIncomingObjectService;
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.ExecutionException;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//
//public class UnidentifiedIncomingObjectServiceImpl implements UnidentifiedIncomingObjectService {
//
//    private final Cache<String, UnidentifiedIncomingObject> objectsHolder;
//
//    public UnidentifiedIncomingObjectServiceImpl(int maxSize) {
//        objectsHolder = CacheBuilder.newBuilder()
//                .maximumSize(maxSize)
//                .build();
//    }
//
//    @Override
//    public void processNewUnidentified(String key) {
//        try {
//            objectsHolder.get(key, () -> new UnidentifiedIncomingObject(key))
//                    .updateStats();
//        } catch (ExecutionException e) {
//            log.error("Error occurred", e);
//        }
//    }
//}
