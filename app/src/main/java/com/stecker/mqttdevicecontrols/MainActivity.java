package com.stecker.mqttdevicecontrols;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.os.Bundle;
import android.service.controls.DeviceTypes;
import android.util.Log;

import com.google.gson.Gson;
import com.stecker.mqttdevicecontrols.settings.Control;
import com.stecker.mqttdevicecontrols.settings.Server;
import com.stecker.mqttdevicecontrols.settings.SettingsAPI;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public String getTestConfig() {
        LinkedList<Server> servers = new LinkedList<>();
        Server s = new Server();

        s.enabled = true;
        s.port = 1883;
        s.url = "192.168.178.25";
        s.protocol = "tcp";
        s.controls.add(new Control());
        s.controls.get(0).enabled = true;
        s.controls.get(0).structure = "Carsten";
        s.controls.get(0).controlID = "0";
        s.controls.get(0).deviceType = DeviceTypes.TYPE_THERMOSTAT;
        s.controls.get(0).MQTTtopic = "home/carsten/deckenlampe/coolness";
        s.controls.get(0).title = "Wärmeregler";
        s.controls.get(0).subtitle = "Schlafzimmer";
        s.controls.get(0).template.templateType = "rangetemplate";
        s.controls.get(0).template.minValue = 0.0f;
        s.controls.get(0).template.maxValue = 100.0f;
        s.controls.get(0).template.stepValue = 1.0f;
        s.controls.get(0).template.formatString = "%.0f";
        s.controls.get(0).PIFlags = PendingIntent.FLAG_UPDATE_CURRENT;

        s.controls.add(new Control());
        s.controls.get(1).enabled = true;
        s.controls.get(1).structure = "Carsten";
        s.controls.get(1).controlID = "1";
        s.controls.get(1).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(1).MQTTtopic = "home/carsten/deckenlampe/brightness";
        s.controls.get(1).title = "Lichtregler";
        s.controls.get(1).subtitle = "Schlafzimmer";
        s.controls.get(1).template.templateType = "rangetemplate";
        s.controls.get(1).template.minValue = 0.0f;
        s.controls.get(1).template.maxValue = 100.0f;
        s.controls.get(1).template.stepValue = 1.0f;
        s.controls.get(1).template.formatString = "%.0f";
        s.controls.get(1).PIFlags = PendingIntent.FLAG_UPDATE_CURRENT;

        s.controls.add(new Control());
        s.controls.get(2).enabled = true;
        s.controls.get(2).structure = "Carsten";
        s.controls.get(2).controlID = "2";
        s.controls.get(2).deviceType = DeviceTypes.TYPE_OUTLET;
        s.controls.get(2).MQTTtopic = "home/carsten/steckdose1/state";
        s.controls.get(2).title = "Steckdose 1";
        s.controls.get(2).subtitle = "Schlafzimmer";
        s.controls.get(2).template.templateType = "toggletemplate";
        s.controls.get(2).template.actionDescription = "BUTTON";
        s.controls.get(2).PIFlags = PendingIntent.FLAG_UPDATE_CURRENT;

        servers.add(s);
        Gson gson = new Gson();
        return gson.toJson(servers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String testConfig = getTestConfig();
        Log.println(Log.ASSERT, "serversJSON", testConfig);
        SettingsAPI s = new SettingsAPI(getFilesDir() + "/" + getString(R.string.config_file));
        try {
            s.saveSettings(getTestConfig());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}