package com.blucharge.ocpp.config.intercepter;


import com.blucharge.ocpp.constants.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@DependsOn("ApplicationConstants")
public class ApplicationSourceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("applicationSource", ApplicationConstants.APPLICATION_ID);
        return true;
    }
}