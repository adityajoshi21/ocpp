package com.blucharge.ocpp.dto.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StopTransactionRequest {

        private String idTag;
        @NotNull (message = "Meter stop value not found in request")
        private BigDecimal meterStopValue;
        @NotNull (message = "Meter stop timestamp not found in request")
        private DateTime timestamp;
        @NotNull (message = "Transaction Id is not found in request")
        private Long transactionId;
        private String connectorName;
        private String reason;
        protected List<MeterValue> transactionData;


        public boolean isSetIdTag() {
                return (this.idTag!=null);
        }

        public boolean isSetTransactionData() {
                return ((this.transactionData!=null) && !(this.transactionData.isEmpty()));
        }
}