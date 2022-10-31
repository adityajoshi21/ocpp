package com.blucharge.ocpp.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class ReportsController {

    @RequestMapping(value = "/online/offline/report",method = RequestMethod.GET)
    public String onlineOfflineReport(@RequestParam("start")Long start,@RequestParam("end")Long end){

        return "";
    }
}
