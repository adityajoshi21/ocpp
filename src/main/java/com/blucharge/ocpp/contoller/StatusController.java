package com.blucharge.ocpp.contoller;

import com.blucharge.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class StatusController {

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseDto<String> status() {
        return new ResponseDto<>("I'm Alive!",200,"SUCCESS",null);
    }
}
