package com.blucharge.ocpp.service;

import com.blucharge.event.dto.RemoteStartCommandDto;
import com.blucharge.event.dto.RemoteStopCommandDto;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionRequest;
import com.blucharge.ocpp.dto.start_transaction.StartTransactionResponse;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionRequest;
import com.blucharge.ocpp.dto.stop_transaction.StopTransactionResponse;

public interface TransactionService {
    StartTransactionResponse startTransaction(StartTransactionRequest request, String chargerName);

    StopTransactionResponse stopTransaction(StopTransactionRequest request, String chargerName);

    void handleRemoteStopCommand(RemoteStopCommandDto remoteStopCommandDto);

    void handleRemoteStartCommand(RemoteStartCommandDto remoteStartCommandDto);
}
