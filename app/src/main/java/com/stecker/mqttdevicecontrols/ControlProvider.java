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
import org.reactivestreams.Subscriber;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

public class ControlProvider extends ControlsProviderService {
    private ReplayProcessor updatePublisher;
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
    /**
     * Publisher for all available controls
     * <p>
     * Retrieve all available controls. Use the stateless builder {@link Control.StatelessBuilder}
     * to build each Control. Call {@link Subscriber#onComplete} when done loading all unique
     * controls, or {@link Subscriber#onError} for error scenarios. Duplicate Controls will
     * replace the original.
     */
    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        initControlProvider();
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(jca.getStatelessDeviceControls(getBaseContext())));
    }

    /**
     * Return a valid Publisher for the given controlIds. This publisher will be asked to provide
     * updates for the given list of controlIds as long as the {@link Subscription} is valid.
     * Calls to {@link Subscriber#onComplete} will not be expected. Instead, wait for the call from
     * {@link Subscription#cancel} to indicate that updates are no longer required. It is expected
     * that controls provided by this publisher were created using {@link Control.StatefulBuilder}.
     *
     * @param controlIds
     */
    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {
        initControlProvider();

        try {
            servers = settingsAPI.getSettingsObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        updatePublisher = ReplayProcessor.create();
        for (Server server : servers) {
            for (com.stecker.mqttdevicecontrols.settings.Control control : server.controls) {
                if (controlIds.contains(control.controlID)) {
                    updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, Control.STATUS_OK, new State()));
                }
            }
        }

        return FlowAdapters.toFlowPublisher(updatePublisher);
    }

    /**
     * The user has interacted with a Control. The action is dictated by the type of
     * {@link ControlAction} that was sent. A response can be sent via
     * {@link Consumer#accept}, with the Integer argument being one of the provided
     * {@link ControlAction.ResponseResult}. The Integer should indicate whether the action
     * was received successfully, or if additional prompts should be presented to
     * the user. Any visual control updates should be sent via the Publisher.
     *
     * @param controlId
     * @param action
     * @param consumer
     */
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
                            String message = "";
                            if (action.getNewState()) {
                                message = control.template.onCommand;
                            } else {
                                message = control.template.offCommand;
                            }

                            if (!message.equals("")) {
                                mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
                                mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, message);
                            }

                            updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, state, new State(action.getNewState())));

                            break;
                        }
                        case "rangetemplate": {
                            FloatAction action = (FloatAction) controlAction;

                            int state = Control.STATUS_OK;

                            // MQTT stuff
                            mqttClient = new MQTTClient(uri, mqttClientID + System.currentTimeMillis());
                            mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, Float.toString(action.getNewValue()));

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
                                mqttClient.sendMqttMessage(getBaseContext(), control.MQTTtopic, message);
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
