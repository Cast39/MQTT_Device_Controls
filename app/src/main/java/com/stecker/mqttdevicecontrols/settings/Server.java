package com.stecker.mqttdevicecontrols.settings;


import java.util.LinkedList;

public class Server {
    public String protocol = "tcp";
    public String url;
    public int port = 1883;
    public String username = "";
    public String password = "";
    public boolean enabled = true;
    public LinkedList<Control> controls = new LinkedList<>();
}
