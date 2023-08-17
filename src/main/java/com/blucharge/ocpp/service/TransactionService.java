package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.ws.StartTransactionRequest;
import com.blucharge.ocpp.dto.ws.StartTransactionResponse;
import com.blucharge.ocpp.dto.ws.StopTransactionRequest;
import com.blucharge.ocpp.dto.ws.StopTransactionResponse;

public interface TransactionService {
    StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerIdentity);

    StopTransactionResponse stopTransaction(StopTransactionRequest request, String chargerId);
}
