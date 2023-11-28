package com.blucharge.ocpp.service;

import com.blucharge.ocpp.dto.api.RemoteStartTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStartTransactionResponse;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionRequest;
import com.blucharge.ocpp.dto.api.RemoteStopTransactionResponse;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionRequest;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionResponse;
import com.blucharge.ocpp.dto.ws.StopTransactionRequest;
import com.blucharge.ocpp.dto.ws.StopTransactionResponse;

public interface TransactionService {
    StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerName);
    StopTransactionResponse stopTransaction(StopTransactionRequest request, String chargerName);
    RemoteStartTransactionResponse remoteStartTransaction(RemoteStartTransactionRequest request, String chargerName);
    RemoteStopTransactionResponse remoteStopTransaction(RemoteStopTransactionRequest request, String chargerName);
}
