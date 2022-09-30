package com.stecker.mqttdevicecontrols.settings;

import android.app.PendingIntent;
import android.service.controls.DeviceTypes;

import com.stecker.mqttdevicecontrols.State;

public class Control {
    public String controlID = "";
    public int deviceType = DeviceTypes.TYPE_LIGHT;
    public boolean enabled = true;
    public String MQTTtopic = "default/topic";
    public String MQTTtopic2;
    public boolean retain = true;
    public String title = "";
    public String subtitle = "";
    public String structure = "Default";
    public int PIFlags = PendingIntent.FLAG_UPDATE_CURRENT; // flags for pending Intent
    public Template template;
    public State state;
}
