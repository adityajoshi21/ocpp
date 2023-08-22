package com.blucharge.ocpp.dto.ws;

import com.blucharge.ocpp.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampledValue {
    protected String value;
    protected ReadingContext context;
    protected ValueFormat format;
    protected Measurand measurand;
    protected Phase phase;
    protected Location location;
    protected String unit;

    public boolean isSetContext() {
        return (this.context!= null);
    }

    public boolean isSetFormat() {
        return (this.format!=null);
    }

    public  boolean isSetMeasurand() {
        return (this.measurand!=null);
    }
    public boolean isSetPhase() {
        return (this.phase!=null);
    }
    public boolean isSetLocation() {
        return  (this.location!=null);
    }
    public boolean isSetUnit() {
        return (this.unit!= null);
    }

}