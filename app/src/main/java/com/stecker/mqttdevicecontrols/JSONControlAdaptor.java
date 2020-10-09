package com.stecker.mqttdevicecontrols;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.controls.Control;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.ToggleTemplate;

import com.stecker.mqttdevicecontrols.settings.Server;

import java.util.LinkedList;

// Reads the JSON server File and creates Device Controls Accordingly
public class JSONControlAdaptor {
    LinkedList<Server> servers;

    public JSONControlAdaptor(LinkedList<Server> servers) {
        this.servers = servers;
    }

    public LinkedList<Control> getStatelessDeviceControls(Context ctx) {
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
        // Extract Controltemplate
        ControlTemplate ct = null;
        Control c = null;
        if (control.template.templateType.equals("toggletemplate")) {
            ct = new ToggleTemplate(
                    control.controlID + control.template.templateType,
                    new ControlButton(false, control.template.actionDescription));

        } else if(control.template.templateType.equals("rangetemplate")) {
            ct = new RangeTemplate(
                    control.controlID + control.template.templateType,
                    control.template.minValue,
                    control.template.maxValue,
                    control.template.minValue + 1,
                    control.template.stepValue,
                    control.template.formatString);

        } else if(control.template.templateType.equals("togglerangetemplate")) {

        } else if(control.template.templateType.equals("temperaturecontroltemplate")) {

        } else if(control.template.templateType.equals("statelesstemplate")) {

        } else {
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
