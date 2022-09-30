package com.stecker.mqttdevicecontrols;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.controls.Control;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.ToggleTemplate;
import android.util.Log;

import com.stecker.mqttdevicecontrols.settings.Server;
import com.stecker.mqttdevicecontrols.settings.SettingsAPI;

import java.io.FileNotFoundException;
import java.util.LinkedList;

// Reads the JSON server File and creates Device Controls Accordingly
public class JSONControlAdaptor {
    SettingsAPI settingsAPI;
    LinkedList<Server> servers;

    public JSONControlAdaptor(SettingsAPI settingsAPI) {
        this.settingsAPI = settingsAPI;
    }

    public LinkedList<Control> getStatelessDeviceControls(Context ctx) {
        try {
            servers = settingsAPI.getSettingsObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LinkedList<Control> deviceControls = new LinkedList<>();
        for (Server server: servers) {
            if (server.enabled) {
                for (com.stecker.mqttdevicecontrols.settings.Control control: server.controls) {
                    if (control.enabled) {
                        PendingIntent pi = PendingIntent.getActivity(ctx, 1, new Intent(), control.PIFlags);
                        Control.StatelessBuilder sb = new Control.StatelessBuilder(control.controlID, pi);

                        sb.setTitle(control.title);
                        sb.setSubtitle(control.subtitle);
                        sb.setStructure(control.structure);
                        sb.setDeviceType(control.deviceType);
                        //TODO sb.setCustomColor()

                        deviceControls.add(sb.build());
                    }
                }
            }
        }
        return deviceControls;
    }

    public Control getStatefulDeviceControl(Context ctx, com.stecker.mqttdevicecontrols.settings.Control control, int status) {
        State state = control.state;
        // Extract Controltemplate
        ControlTemplate ct = null;

        switch (control.template.templateType) {
            case "toggletemplate":
                //Log.println(Log.ASSERT, "jca", "toggletemplate state: " + state.booleanState);
                ct = new ToggleTemplate(
                        control.controlID + control.template.templateType,
                        new ControlButton(state.booleanState, control.template.actionDescription));

                break;
            case "rangetemplate":
                //Log.println(Log.ASSERT, "jca", "rangetemplate state: " + state.floatState);
                ct = new RangeTemplate(
                        control.controlID + control.template.templateType,
                        control.template.minValue,
                        control.template.maxValue,
                        state.floatState,
                        control.template.stepValue,
                        control.template.formatString);


                break;
            case "togglerangetemplate":
                //TODO
                Log.println(Log.ASSERT, "TODO", control.template.templateType + " is not supported!");

                break;
            case "temperaturecontroltemplate":
                //TODO
                Log.println(Log.ASSERT, "TODO", control.template.templateType + " is not supported!");

                break;
            case "statelesstemplate":
                ct = new StatelessTemplate(control.controlID + control.template.templateType);

                break;
            default:
                Log.println(Log.ASSERT, "TODO", control.template.templateType + " is not supported!");
                return null;
        }
        PendingIntent pi = PendingIntent.getActivity(ctx, 1, new Intent(), control.PIFlags);
        Control.StatefulBuilder sb = new Control.StatefulBuilder(control.controlID, pi);

        sb.setTitle(control.title);
        sb.setSubtitle(control.subtitle);
        sb.setStructure(control.structure);
        sb.setDeviceType(control.deviceType);
        sb.setControlTemplate(ct);
        sb.setStatus(status);
        //TODO sb.setCustomColor()
        return sb.build();
    }
}
