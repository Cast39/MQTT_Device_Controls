package com.stecker.mqttdevicecontrols.settings;

public class ToggleRangetemplate extends Template {
    public ToggleRangetemplate() {
        super.templateType = "togglerangetemplate";
        super.onCommand = "true";
        super.offCommand = "false";

        super.minValue = 0.0f;
        super.maxValue = 100.0f;
        super.stepValue = 1.0f;
        super.formatString = "%.0f"; // eg 100.0 / 50.0
    }
}
