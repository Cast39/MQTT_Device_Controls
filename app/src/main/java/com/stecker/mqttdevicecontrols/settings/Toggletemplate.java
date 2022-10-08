package com.stecker.mqttdevicecontrols.settings;

public class Toggletemplate extends Template {
    public Toggletemplate() {
        super.templateType = "toggletemplate";
        super.onCommand = "true";
        super.offCommand = "false";
    }
}
