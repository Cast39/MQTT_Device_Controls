package com.stecker.mqttdevicecontrols;

import android.content.Context;
import android.service.controls.Control;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import io.reactivex.processors.ReplayProcessor;


public class MQTTClient {
    final private String serverUri;
    final private String clientId;
    private MqttConnectOptions mqttConnectOptions;
    private DisconnectedBufferOptions disconnectedBufferOptions;

    public MQTTClient(String serverUri, String clientId) {
        this.serverUri = serverUri;
        this.clientId = clientId;

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setConnectionTimeout(5);

        disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);

    }

    // sends messages asynchronous
    public void sendMqttMessage(Context ctx, final String topic, final String message, final boolean retain) {
        //Log.println(Log.ASSERT, topic, "Sending Message to " + topic + ": " + message + " retained=" + retain);
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(ctx, serverUri, clientId);



        try {
            MqttMessage m = new MqttMessage();
            m.setPayload(message.getBytes());

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    try {
                        mqttAndroidClient.publish(topic, message.getBytes(), 0, retain);
                        Thread.sleep(1000);
                        mqttAndroidClient.disconnect();
                        Log.println(Log.ASSERT, topic, "sent!");
                    } catch (MqttException | InterruptedException e) {
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

    public void receiveRetainedMessages(Context ctx, JSONControlAdaptor jca, ReplayProcessor<Control> updatePublisher, ArrayList<com.stecker.mqttdevicecontrols.settings.Control> controlSettings) {
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(ctx, serverUri, clientId);


        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.println(Log.ASSERT,"Mqtt","Failed to connect to: " + serverUri + exception.toString());
                }
            });


            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    try {
                        for (com.stecker.mqttdevicecontrols.settings.Control controlSetting:controlSettings) {
                            mqttAndroidClient.subscribe(controlSetting.MQTTtopic, 0);

                            if (!(controlSetting.MQTTtopic2 == null || "".equals(controlSetting.MQTTtopic2))) {
                                mqttAndroidClient.subscribe(controlSetting.MQTTtopic2, 0);
                            }
                        }

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.println(Log.ASSERT,"Mqtt","Connection lost for receiving client");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    Log.println(Log.ASSERT,"Mqtt","Incoming message from [" + topic + "]: " + payload);

                    for (com.stecker.mqttdevicecontrols.settings.Control controlSetting:controlSettings) {

                        if (controlSetting.MQTTtopic.equals(topic) || topic.equals(controlSetting.MQTTtopic2)) {
                            // Log.println(Log.ASSERT, "Mqtt", "processing...");
                            if (controlSetting.state.autodecode(payload)) {
                                // Log.println(Log.ASSERT,"Mqtt","Decoded successful!");

                                int state = Control.STATUS_OK;
                                updatePublisher.onNext(jca.getStatefulDeviceControl(ctx, controlSetting, state));

                                //Log.println(Log.ASSERT,"Mqtt","Updated ControlTemplate to " + s.autodecode);
                            } else {
                                Log.println(Log.ASSERT,"Mqtt","Error while decoding \"" + payload + "\" from [" + topic + "]");

                            }

                        }

                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();

        }

    }

}