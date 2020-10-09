package com.stecker.mqttdevicecontrols.settings;

public class Control {
    public String controlID;
    public int deviceType;
    public String MQTTtopic;
    public String title;
    public String subtitle;
    public Template template = new Template();
}
