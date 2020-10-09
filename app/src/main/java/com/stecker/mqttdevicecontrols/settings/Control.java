package com.stecker.mqttdevicecontrols.settings;

public class Control {
    public String controlID;
    public int deviceType;
    public boolean enabled;
    public String MQTTtopic;
    public String title;
    public String subtitle;
    public String structure;
    public int PIFlags; // flags for pending Intent
    public Template template = new Template();
}
