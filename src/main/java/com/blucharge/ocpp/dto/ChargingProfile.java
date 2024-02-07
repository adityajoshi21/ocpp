package com.blucharge.ocpp.dto;

import com.blucharge.ocpp.enums.ChargingProfileKindType;
import com.blucharge.ocpp.enums.ChargingProfilePurposeType;
import com.blucharge.ocpp.enums.RecurrencyKindType;
import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargingProfile {
    private Integer chargingProfileId;
    private Integer transactionId;
    private Integer stackLevel;
    private ChargingProfilePurposeType chargingProfilePurpose;
    private ChargingProfileKindType chargingProfileKind;
    private RecurrencyKindType recurrencyKind;
    private DateTime validFrom;
    private DateTime validTo;
    private ChargingSchedule chargingSchedule;
}
