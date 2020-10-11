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
        s.url = "test.mosquitto.org";

        s.controls.add(new Control());
        s.controls.get(0).structure = "Carsten";
        s.controls.get(0).controlID = "0";
        s.controls.get(0).deviceType = DeviceTypes.TYPE_THERMOSTAT;
        s.controls.get(0).MQTTtopic = "home/carsten/deckenlampe/coolness";
        s.controls.get(0).title = "Wärme";
        s.controls.get(0).subtitle = "Deckenlampe";
        s.controls.get(0).template.templateType = "rangetemplate";

        s.controls.add(new Control());
        s.controls.get(1).structure = "Carsten";
        s.controls.get(1).controlID = "1";
        s.controls.get(1).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(1).MQTTtopic = "home/carsten/deckenlampe/brightness";
        s.controls.get(1).title = "Helligkeit";
        s.controls.get(1).subtitle = "Deckenlampe";
        s.controls.get(1).template.templateType = "rangetemplate";

        s.controls.add(new Control());
        s.controls.get(2).structure = "Carsten";
        s.controls.get(2).controlID = "2";
        s.controls.get(2).deviceType = DeviceTypes.TYPE_OUTLET;
        s.controls.get(2).MQTTtopic = "home/carsten/steckdose1/state";
        s.controls.get(2).title = "\uD83D\uDE3B";
        s.controls.get(2).template.templateType = "toggletemplate";
        s.controls.get(2).template.actionDescription = "BUTTON";

        s.controls.add(new Control());
        s.controls.get(3).structure = "Clemens";
        s.controls.get(3).controlID = "3";
        s.controls.get(3).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(3).MQTTtopic = "home/clemens/deckenlampe/state";
        s.controls.get(3).title = "\uD83D\uDE3B";
        s.controls.get(3).template.templateType = "toggletemplate";
        s.controls.get(3).template.actionDescription = "BUTTON";

        s.controls.add(new Control());
        s.controls.get(4).structure = "Living Room";
        s.controls.get(4).controlID = "4";
        s.controls.get(4).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(4).MQTTtopic = "home/living_room/ceilinglamp/state";
        s.controls.get(4).title = "Deckenlampe";
        s.controls.get(4).template.templateType = "toggletemplate";
        s.controls.get(4).template.actionDescription = "BUTTON";

        s.controls.add(new Control());
        s.controls.get(5).structure = "Dining Room";
        s.controls.get(5).controlID = "5";
        s.controls.get(5).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(5).MQTTtopic = "home/dining_room/ceilinglamp/state";
        s.controls.get(5).title = "Deckenlampe";
        s.controls.get(5).template.templateType = "toggletemplate";
        s.controls.get(5).template.actionDescription = "BUTTON";

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