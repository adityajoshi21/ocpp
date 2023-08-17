package com.blucharge.ocpp.dto.ws;

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
        @NotNull
        private BigDecimal meterStop;
        @NotNull
        private DateTime timestamp;
        @NotNull
        private Long transactionId;
        private String reason;
        protected List<MeterValue> transactionData;


        public boolean isSetIdTag() {
            return (this.idTag!=null);
        }

        public boolean isSetTransactionData() {
            return ((this.transactionData!=null) && !(this.transactionData.isEmpty()));
        }
}
