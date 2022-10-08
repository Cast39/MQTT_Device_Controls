package com.stecker.mqttdevicecontrols.settings;

public class MQTTtopic {
    public String send;
    public String recv;
    public String topic;

    public MQTTtopic() {
    }

    public MQTTtopic(String send, String recv) {
        this.send = send;
        this.recv = recv;
    }

    public MQTTtopic(String topic) {
        this.topic = topic;
    }

    public String getRecv() {
        if (recv == null) return topic;

        return recv;
    }

    public String getSend() {
        if (send == null) return topic;

        return send;
    }
}
