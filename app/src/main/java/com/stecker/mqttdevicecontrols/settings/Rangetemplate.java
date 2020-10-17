package com.stecker.mqttdevicecontrols.settings;

public class Rangetemplate extends Template {
    public Rangetemplate() {
        super.templateType = "rangetemplate";
        super.minValue = 0.0f;
        super.maxValue = 100.0f;
        super.stepValue = 1.0f;
        super.formatString = "%.0f"; // eg 100.0 / 50.0
    }
}
