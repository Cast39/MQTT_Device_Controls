package com.stecker.mqttdevicecontrols.settings;

public class Toggletemplate extends Template {
    public Toggletemplate() {
        super.templateType = "toggletemplate";
        super.actionDescription = "";
        super.onValue = "true";
        super.offValue = "false";
    }
}
