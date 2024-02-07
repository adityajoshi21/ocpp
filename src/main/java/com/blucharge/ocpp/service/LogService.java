package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.blucgn.OcppSocketDataFromBlucgnDto;

public interface LogService {
    void handleIncomingMessage(OcppSocketDataFromBlucgnDto ocppSocketDataFromBlucgnDto);
}
