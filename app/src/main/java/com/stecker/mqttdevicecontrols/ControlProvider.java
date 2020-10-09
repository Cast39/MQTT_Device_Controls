package com.stecker.mqttdevicecontrols;

import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.actions.ControlAction;

import androidx.annotation.NonNull;

import com.stecker.mqttdevicecontrols.settings.SettingsAPI;

import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscriber;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import io.reactivex.Flowable;

public class ControlProvider extends ControlsProviderService {
    SettingsAPI settingsAPI;
    JSONControlAdaptor jca;
    String filepath = getFilesDir() + "/";
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
        settingsAPI = new SettingsAPI(filepath + getString(R.string.config_file));
        try {
            jca = new JSONControlAdaptor(settingsAPI.getSettingsObject());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(jca.getDeviceControls()));
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
        return null;
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
    public void performControlAction(@NonNull String controlId, @NonNull ControlAction action, @NonNull Consumer<Integer> consumer) {

    }
}
