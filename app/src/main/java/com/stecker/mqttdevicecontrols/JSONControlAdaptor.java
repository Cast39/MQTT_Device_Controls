package com.stecker.mqttdevicecontrols;

import android.service.controls.Control;

import com.stecker.mqttdevicecontrols.settings.Server;

import java.util.LinkedList;

// Reads the JSON server File and creates Device Controls Accordingly
public class JSONControlAdaptor {
    LinkedList<Server> servers;

    public JSONControlAdaptor(LinkedList<Server> servers) {
        this.servers = servers;
    }

    public LinkedList<Control> getDeviceControls() {
        LinkedList<Control> deviceControls = new LinkedList<>();
        //TODO
        return deviceControls;
    }
}
