package com.stecker.mqttdevicecontrols.settings;


import java.util.LinkedList;

public class Server {
    public String protocol;
    public String url;
    public int port;
    public String username;
    public String password;
    public boolean enabled;
    public LinkedList<Structure> structures = new LinkedList<>();
}
