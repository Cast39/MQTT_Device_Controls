package com.stecker.mqttdevicecontrols;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.controls.DeviceTypes;
import android.service.controls.templates.ToggleTemplate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stecker.mqttdevicecontrols.settings.Control;
import com.stecker.mqttdevicecontrols.settings.Rangetemplate;
import com.stecker.mqttdevicecontrols.settings.Server;
import com.stecker.mqttdevicecontrols.settings.SettingsAPI;
import com.stecker.mqttdevicecontrols.settings.Statelesstemplate;
import com.stecker.mqttdevicecontrols.settings.Toggletemplate;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public String configFile;
    public Gson gson = new Gson();

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
        s.controls.get(0).template = new Rangetemplate();

        s.controls.add(new Control());
        s.controls.get(1).structure = "Carsten";
        s.controls.get(1).controlID = "1";
        s.controls.get(1).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(1).MQTTtopic = "home/carsten/deckenlampe/brightness";
        s.controls.get(1).title = "Helligkeit";
        s.controls.get(1).subtitle = "Deckenlampe";
        s.controls.get(1).template = new Rangetemplate();

        s.controls.add(new Control());
        s.controls.get(2).structure = "Carsten";
        s.controls.get(2).controlID = "2";
        s.controls.get(2).deviceType = DeviceTypes.TYPE_OUTLET;
        s.controls.get(2).MQTTtopic = "home/carsten/steckdose1/state";
        s.controls.get(2).title = "\uD83D\uDE3B";
        s.controls.get(2).template = new Toggletemplate();
        s.controls.get(2).template.actionDescription = "BUTTON";

        s.controls.add(new Control());
        s.controls.get(3).structure = "Clemens";
        s.controls.get(3).controlID = "3";
        s.controls.get(3).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(3).MQTTtopic = "home/clemens/deckenlampe/state";
        s.controls.get(3).title = "\uD83D\uDE3B";
        s.controls.get(3).template = new Toggletemplate();
        s.controls.get(3).template.actionDescription = "BUTTON";
        s.controls.get(3).template.onCommand = "on";
        s.controls.get(3).template.offCommand = "off";

        s.controls.add(new Control());
        s.controls.get(4).structure = "Living Room";
        s.controls.get(4).controlID = "4";
        s.controls.get(4).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(4).MQTTtopic = "home/living_room/ceilinglamp/state";
        s.controls.get(4).title = "Deckenlampe";
        s.controls.get(4).template = new Toggletemplate();
        s.controls.get(4).template.actionDescription = "BUTTON";

        s.controls.add(new Control());
        s.controls.get(5).structure = "Dining Room";
        s.controls.get(5).controlID = "5";
        s.controls.get(5).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(5).MQTTtopic = "home/dining_room/ceilinglamp/state";
        s.controls.get(5).title = "ToggleDeckenlampe";
        s.controls.get(5).template = new Toggletemplate();
        s.controls.get(5).template.actionDescription = "BUTTON";
        s.controls.get(5).template.onCommand = "switch mode!";
        s.controls.get(5).template.offCommand = "";

        s.controls.add(new Control());
        s.controls.get(6).structure = "Dining Room";
        s.controls.get(6).controlID = "6";
        s.controls.get(6).deviceType = DeviceTypes.TYPE_LIGHT;
        s.controls.get(6).MQTTtopic = "home/dining_room/ceilinglamp/state2";
        s.controls.get(6).title = "StatelessDeckenlampe";
        s.controls.get(6).template = new Statelesstemplate();
        s.controls.get(6).template.command = "switch mode!";

        servers.add(s);
        Gson gson = new Gson();
        return gson.toJson(servers);
    }
    public SettingsAPI initConfigFile() {
        configFile = getFilesDir() + "/" + getString(R.string.config_file);
        SettingsAPI settingsAPI = new SettingsAPI(configFile);
        try {
            settingsAPI.getSettingsObject();
            Log.println(Log.ASSERT, "Config File", "Found existing config file!");
        } catch (FileNotFoundException e) {
            Log.println(Log.ASSERT, "ConfigFile", "Couldn't find existing config file!");

            if (settingsAPI.saveSettings(getTestConfig())) {
                Log.println(Log.ASSERT, "ConfigFile", "Created default config file!");
            } else {
                Log.println(Log.ASSERT, "ConfigFile", "Unable to create config file!");
            }
        }
        return settingsAPI;
    }

    public void initUI(final SettingsAPI s) {
        EditText editor = findViewById(R.id.configfileeditor);
        try {
            editor.setText(s.getSettingsText(true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editor = findViewById(R.id.configfileeditor);
                String text = editor.getText().toString();
                try {
                    gson.fromJson(text, Object.class);
                    s.saveSettings(text);
                    Log.println(Log.ASSERT, "Settings", "Settings Saved!");
                    Toast.makeText(getBaseContext(), "Settings Saved!", Toast.LENGTH_SHORT).show();
                } catch (JsonSyntaxException jse) {
                    Log.println(Log.ASSERT, "Settings", "HOW DARE YOU");
                    Toast.makeText(getBaseContext(), "Invalid JSON!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.saveSettings(getTestConfig());
                EditText editor = findViewById(R.id.configfileeditor);
                try {
                    editor.setText(s.getSettingsText(true));
                } catch (FileNotFoundException e) {
                    editor.setText("Error while reading Settings");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SettingsAPI s = initConfigFile();

        initUI(s);
    }
}