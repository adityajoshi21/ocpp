package com.blucharge.ocpp.config.intercepter;



import com.blucharge.ocpp.constants.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
public class ApplicationSourceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        request.setAttribute("applicationSource", ApplicationConstants.APPLICATION_ID);
        request.setAttribute("applicationClientId", ApplicationConstants.CLIENT_ID);
        request.setAttribute("applicationClientSecret", ApplicationConstants.CLIENT_SECRET);
        request.setAttribute("requestUUID", UUID.randomUUID().toString());
        request.setAttribute("applicationUsername", ApplicationConstants.USERNAME);
        request.setAttribute("applicationPassword", ApplicationConstants.PASSWORD);

        System.setProperty("applicationSource", ApplicationConstants.APPLICATION_ID);
        System.setProperty("applicationClientId", ApplicationConstants.CLIENT_ID);
        System.setProperty("applicationClientSecret", ApplicationConstants.CLIENT_SECRET);
        System.setProperty("applicationUsername", ApplicationConstants.USERNAME);
        System.setProperty("applicationPassword", ApplicationConstants.PASSWORD);
        return true;
    }

}