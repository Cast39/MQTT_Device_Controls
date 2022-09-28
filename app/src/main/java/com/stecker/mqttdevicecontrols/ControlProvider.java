package com.stecker.mqttdevicecontrols;

import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stecker.mqttdevicecontrols.settings.Server;
import com.stecker.mqttdevicecontrols.settings.SettingsAPI;

import org.reactivestreams.FlowAdapters;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

public class ControlProvider extends ControlsProviderService {
    private ReplayProcessor<Control> updatePublisher;
    SettingsAPI settingsAPI = null;
    LinkedList<Server> servers;
    JSONControlAdaptor jca = null;
    String filepath = null;
    String mqttClientID = "Temporär von externer Stromversorgung unabhängiges Gerät zu Sprachfernübertragung";

    public void initControlProvider() {
        if (filepath == null) {
            filepath = getBaseContext().getFilesDir() + "/";
            Log.println(Log.ASSERT, "INFO", "Filepath: " + filepath);
        }
        if (settingsAPI == null) {
            settingsAPI = new SettingsAPI(filepath + getString(R.string.config_file));
        }
        if (jca == null) {
            jca = new JSONControlAdaptor(settingsAPI);
        }

    }


    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        initControlProvider();
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(jca.getStatelessDeviceControls(getBaseContext())));
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {
        initControlProvider();

        // read config
        try {
            servers = settingsAPI.getSettingsObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return FlowAdapters.toFlowPublisher(updatePublisher);
        }

        // open mqtt
        MQTTClient mqttClient;


        updatePublisher = ReplayProcessor.create();
        State s;
        for (Server server : servers) {
            // generate all visible Controls with default states
            for (com.stecker.mqttdevicecontrols.settings.Control control : server.controls) {
                if (controlIds.contains(control.controlID)) {
                    s = new State();
                    //Log.println(Log.ASSERT, "Link", "creating specific publisher for" + control.controlID);

                    updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, Control.STATUS_OK, s));

                }
            }



            String uri = server.protocol + "://" + server.url + ":" + server.port;
            mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
            ArrayList<com.stecker.mqttdevicecontrols.settings.Control> controlSettings = new ArrayList<>();

            // extract mqtt topics
            for (com.stecker.mqttdevicecontrols.settings.Control control : server.controls) {
                if (controlIds.contains(control.controlID) && control.retain) {
                    controlSettings.add(control);
                }
            }

            // receive retained messages asynchronously
            Log.println(Log.ASSERT, "Link", "receiving retained states");
            mqttClient.receiveRetainedMessages(getBaseContext(), jca, updatePublisher, controlSettings);
        }

        return FlowAdapters.toFlowPublisher(updatePublisher);
    }


    @Override
    public void performControlAction(@NonNull String controlId, @NonNull ControlAction controlAction, @NonNull Consumer<Integer> consumer) {
        initControlProvider();
        LinkedList<Server> servers;
        MQTTClient mqttClient;
        try {
            servers = settingsAPI.getSettingsObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (Server server : servers) {
            for (final com.stecker.mqttdevicecontrols.settings.Control control : server.controls) {
                if (controlId.equals(control.controlID)) {
                    Log.println(Log.ASSERT, "TAG", controlId);
                    consumer.accept(ControlAction.RESPONSE_OK);
                    String uri = server.protocol + "://" + server.url + ":" + server.port;

                    switch (control.template.templateType) {
                        case "toggletemplate": {
                            BooleanAction action = (BooleanAction) controlAction;
                            int state = Control.STATUS_OK;

                            Log.println(Log.ASSERT, "Link", uri);

                            // MQTT stuff
                            String message;
                            if (action.getNewState()) {
                                message = control.template.onCommand;
                            } else {
                                message = control.template.offCommand;
                            }

                            if (!message.equals("")) {
                                mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
                                mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, message, control.retain);
                            }

                            updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, state, new State(action.getNewState())));

                            break;
                        }
                        case "rangetemplate": {
                            FloatAction action = (FloatAction) controlAction;

                            int state = Control.STATUS_OK;

                            // MQTT stuff
                            mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
                            mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, Float.toString(action.getNewValue()), control.retain);

                            updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, state, new State(action.getNewValue())));

                            break;
                        }
                        case "togglerangetemplate":
                            //TODO
                            break;
                        case "temperaturecontroltemplate":
                            //TODO
                            break;
                        case "statelesstemplate":
                            int state = Control.STATUS_OK;

                            Log.println(Log.ASSERT, "Link", uri);

                            // MQTT stuff
                            String message = control.template.command;

                            if (!message.equals("")) {
                                mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
                                mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, message, control.retain);
                            }

                            updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, state, new State()));
                            break;
                        default:
                            continue;
                    }
                    break;
                }
            }
        }
    }
}
