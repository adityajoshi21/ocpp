package com.blucharge.ocpp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OcppProtocol {

    V_16_JSON(OcppVersion.V_16, OcppTransport.JSON);

    private final OcppVersion version;
    private final OcppTransport transport;

    public static OcppProtocol fromCompositeValue(String v) {

        if (org.apache.commons.lang3.StringUtils.isBlank(v)) {
            return V_16_JSON;
        }

        // If we, in the future, decide to use values
        // containing more than one character for OcppTransport,
        // this will break.

        int splitIndex = v.length() - 1;

        String version = v.substring(0, splitIndex);
        String transport = String.valueOf(v.charAt(splitIndex));

        OcppVersion ov = OcppVersion.fromValue(version);
        OcppTransport ot = OcppTransport.fromValue(transport);

        for (OcppProtocol c : OcppProtocol.values()) {
            if (c.getVersion() == ov && c.getTransport() == ot) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String getCompositeValue() {
        return version.getValue() + transport.getValue();
    }
}

