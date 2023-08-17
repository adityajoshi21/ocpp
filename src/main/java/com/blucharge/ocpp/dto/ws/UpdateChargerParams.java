package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.OcppProtocol;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;
@Getter
@Builder
public class UpdateChargerParams {
    private final OcppProtocol ocppProtocol;
    private  DateTime heartbeatTimestamp;
    private final String vendor, model, pointSerial, boxSerial, fwVersion,
            iccid, imsi, meterType, meterSerial, chargerId;
}
