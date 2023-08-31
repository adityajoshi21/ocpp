package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.api.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.ws.StartTransactionRequest;
import com.blucharge.ocpp.dto.ws.StartTransactionResponse;
import com.blucharge.ocpp.dto.ws.StopTransactionRequest;
import com.blucharge.ocpp.dto.ws.StopTransactionResponse;

public interface TransactionService {
    StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerIdentity);
    StopTransactionResponse stopTransaction(StopTransactionRequest request, String chargerIdentity);
    RemoteStartTransactionResponse remoteStartTransaction(RemoteStartTransactionRequest request, String chargerIdentity);
    RemoteStopTransactionResponse remoteStopTransaction(RemoteStopTransactionRequest request, String chargerIdentity);
}
