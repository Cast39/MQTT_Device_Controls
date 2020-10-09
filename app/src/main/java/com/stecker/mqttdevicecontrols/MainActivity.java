package com.stecker.mqttdevicecontrols;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.stecker.mqttdevicecontrols.settings.Control;
import com.stecker.mqttdevicecontrols.settings.Server;
import com.stecker.mqttdevicecontrols.settings.SettingsAPI;
import com.stecker.mqttdevicecontrols.settings.Structure;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import static android.util.Log.ASSERT;

public class MainActivity extends AppCompatActivity {
    public String getTestConfig() {
        LinkedList<Server> servers = new LinkedList<>();
        Server s = new Server();

        s.enabled = true;
        s.port = 1883;
        s.url = "192.178.178.25";
        s.protocol = "tcp";
        s.structures.add(new Structure());
        s.structures.get(0).name = "Carsten";
        s.structures.get(0).controls.add(new Control());
        s.structures.get(0).controls.get(0).controlID = "0";
        s.structures.get(0).controls.get(0).deviceType = 8;
        s.structures.get(0).controls.get(0).MQTTtopic = "home/carsten/deckenlampe";
        s.structures.get(0).controls.get(0).title = "Lichtschalter";
        s.structures.get(0).controls.get(0).subtitle = "Schlafzimmer";
        s.structures.get(0).controls.get(0).template.templateType = "rangetemplate";
        s.structures.get(0).controls.get(0).template.templateID = "0rangetemplate";
        s.structures.get(0).controls.get(0).template.minValue = 0.0;
        s.structures.get(0).controls.get(0).template.maxValue = 100.0;
        s.structures.get(0).controls.get(0).template.stepValue = 1.0;
        s.structures.get(0).controls.get(0).template.formatString = "%.0f";
        servers.add(s);
        servers.add(s);
        Gson gson = new Gson();
        return gson.toJson(servers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String testConfig = getTestConfig();
        Log.println(ASSERT, "serversJSON", testConfig);
        SettingsAPI s = new SettingsAPI(getFilesDir() + "/testConfig.json");
        try {
            //Log.println(ASSERT, "TEST", "Saving File");
            //s.saveSettings(testConfig);

            Log.println(ASSERT, "TEST", "Reading File");
            Log.println(ASSERT, "TEST", s.getSettingsObject().getFirst().url);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}