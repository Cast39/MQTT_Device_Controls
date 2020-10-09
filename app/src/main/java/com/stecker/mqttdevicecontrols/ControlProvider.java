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
    JSONControlAdaptor jca = null;
    String filepath = null;
    LinkedList<Server> servers = null;

    public void initControlProvider() {
        if (filepath == null) {
            filepath = getBaseContext().getFilesDir() + "/";
            Log.println(Log.ASSERT, "INFO", "Filepath: " + filepath);
        }
        if (settingsAPI == null) {
            settingsAPI = new SettingsAPI(filepath + getString(R.string.config_file));
        }
        if (servers == null) {
            try {
                servers = settingsAPI.getSettingsObject();
            } catch (FileNotFoundException e) {
                Log.e("Settingsfile", "Settingsfile not Found!");
            }
        }
        if (jca == null) {
            jca = new JSONControlAdaptor(servers);
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
        updatePublisher = ReplayProcessor.create();
        for (Server server : servers) {
            for (com.stecker.mqttdevicecontrols.settings.Control control : server.controls) {
                if (controlIds.contains(control.controlID)) {
                    updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, Control.STATUS_OK, new State(false)));
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
        LinkedList<Server> servers;
        try {
            servers = settingsAPI.getSettingsObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (Server server: servers) {
            for (com.stecker.mqttdevicecontrols.settings.Control control: server.controls) {
                if (controlId.equals(control.controlID)) {
                    consumer.accept(ControlAction.RESPONSE_OK);

                    if (control.template.templateType.equals("toggletemplate")) {
                        BooleanAction action = (BooleanAction) controlAction;
                        Log.println(Log.ASSERT, "ButtonEvent", Integer.toString(action.getActionType()));
                        Log.println(Log.ASSERT, "ButtonEvent", Boolean.toString(action.getNewState()));

                        updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, Control.STATUS_OK, new State(action.getNewState())));

                    } else if(control.template.templateType.equals("rangetemplate")) {
                        FloatAction action = (FloatAction) controlAction;
                        Log.println(Log.ASSERT, "ButtonEvent", Integer.toString(action.getActionType()));
                        Log.println(Log.ASSERT, "ButtonEvent", Float.toString(action.getNewValue()));
                        updatePublisher.onNext(jca.getStatefulDeviceControl(getBaseContext(), control, Control.STATUS_OK, new State(action.getNewValue())));

                    } else if(control.template.templateType.equals("togglerangetemplate")) {
                        //TODO
                    } else if(control.template.templateType.equals("temperaturecontroltemplate")) {
                        //TODO
                    } else if(control.template.templateType.equals("statelesstemplate")) {
                        //TODO
                    } else {
                        continue;
                    }
                }
            }
        }
    }
}
