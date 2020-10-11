package com.stecker.mqttdevicecontrols;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTClient {
    private String serverUri;
    private String clientId;

    public MQTTClient(String serverUri, String clientId) {
        this.serverUri = serverUri;
        this.clientId = clientId;
    }

    public void sendMqttMessage(Context ctx, final String topic, final String message) {
        Log.println(Log.ASSERT, topic, "Sending Message");
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(ctx, serverUri, clientId);

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setConnectionTimeout(5);

        try {
            MqttMessage m = new MqttMessage();
            m.setPayload(message.getBytes());

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    try {
                        mqttAndroidClient.publish(topic, message.getBytes(), 0, false);
                        mqttAndroidClient.disconnect();
                        Log.println(Log.ASSERT, topic, "sent!");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.println(Log.ASSERT,"Mqtt","Failed to connect to: " + serverUri + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
