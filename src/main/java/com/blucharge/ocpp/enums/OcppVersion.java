package com.blucharge.ocpp.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OcppVersion {
    V_12("ocpp1.2"),
    V_15("ocpp1.5"),
    V_16("ocpp1.6");

    private final String value;

    public static OcppVersion fromValue(String v) {
        for (OcppVersion version: OcppVersion.values()) {
            if (version.getValue().equals(v)) {
                return version;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

