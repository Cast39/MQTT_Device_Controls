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
import com.stecker.mqttdevicecontrols.settings.ToggleRangetemplate;
import com.stecker.mqttdevicecontrols.settings.Toggletemplate;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public String configFile;
    public Gson gson = new Gson();

    public LinkedList<Server> getBaseConfig() {
        LinkedList<Server> servers = new LinkedList<>();
        Server s = new Server();
        s.url = "public.mqtthq.com";

        Control c = new Control();
        c.structure = "Category1";
        c.controlID = "0";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/category1/light/state";
        c.title = "Light";
        c.subtitle = "State";
        c.template = new Toggletemplate();
        s.controls.add(c);

        servers.add(s);
        return servers;
    }

    public LinkedList<Server> getTestConfig() {
        LinkedList<Server> servers = new LinkedList<>();
        Server s = new Server();
        s.url = "test.mosquitto.org";

        Control c = new Control();
        
        c.structure = "Room1";
        c.controlID = "0";
        c.deviceType = DeviceTypes.TYPE_THERMOSTAT;
        c.MQTTtopic = "home/room1/ceilinglight/warmth";
        c.title = "Ceiling Light";
        c.subtitle = "warmth";
        c.template = new Rangetemplate();
        s.controls.add(c);

        c = new Control();
        c.structure = "Room1";
        c.controlID = "1";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/room1/ceilinglight/brightness";
        c.title = "Ceiling Light";
        c.subtitle = "brightness";
        c.template = new Rangetemplate();
        s.controls.add(c);

        c = new Control();
        c.structure = "Room1";
        c.controlID = "2";
        c.deviceType = DeviceTypes.TYPE_OUTLET;
        c.MQTTtopic = "home/room1/outlet1/state";
        c.title = "\uD83D\uDE3B";
        c.template = new Toggletemplate();
        c.template.actionDescription = "BUTTON";
        s.controls.add(c);

        c = new Control();
        c.structure = "Room2";
        c.controlID = "3";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/room2/ceilinglight/state";
        c.title = "\uD83D\uDE3B";
        c.template = new Toggletemplate();
        c.template.actionDescription = "BUTTON";
        c.template.onCommand = "on";
        c.template.offCommand = "off";
        s.controls.add(c);

        c = new Control();
        c.structure = "Room2";
        c.controlID = "4";
        c.deviceType = DeviceTypes.TYPE_FAN;
        c.MQTTtopic = "home/room2/fan/speed";
        c.MQTTtopic2 = "home/room2/fan/state";
        c.title = "Fan";
        c.subtitle = "ToggleRange";
        c.template = new ToggleRangetemplate();
        c.template.actionDescription = "BUTTON";
        c.template.onCommand = "on";
        c.template.offCommand = "off";
        s.controls.add(c);

        c = new Control();
        c.structure = "Living Room";
        c.controlID = "5";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/living_room/ceilinglight/state";
        c.title = "Ceiling Light";
        c.template = new Toggletemplate();
        c.template.actionDescription = "BUTTON";
        s.controls.add(c);

        c = new Control();
        c.structure = "Dining Room";
        c.controlID = "6";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/dining_room/ceilinglight/state";
        c.title = "Ceiling Light";
        c.template = new Toggletemplate();
        c.template.actionDescription = "BUTTON";
        c.template.onCommand = "switch mode!";
        c.template.offCommand = "";
        s.controls.add(c);

        c = new Control();
        c.structure = "Dining Room";
        c.controlID = "7";
        c.deviceType = DeviceTypes.TYPE_LIGHT;
        c.MQTTtopic = "home/dining_room/TV/togglePower";
        c.title = "Stateless ceilinglight 2";
        c.template = new Statelesstemplate();
        c.template.command = "on";
        s.controls.add(c);


        servers.add(s);
        return servers;
    }

    public SettingsAPI initConfigFile() {
        configFile = getFilesDir() + "/" + getString(R.string.config_file);
        SettingsAPI settingsAPI = new SettingsAPI(configFile);
        try {
            settingsAPI.getSettingsObject();
            Log.println(Log.ASSERT, "Config File", "Found existing config file!");
        } catch (FileNotFoundException e) {
            Log.println(Log.ASSERT, "ConfigFile", "Couldn't find existing config file!");

            if (settingsAPI.saveSettings(gson.toJson(getTestConfig()))) {
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
            editor.setText(s.JSONBeautyfier(s.getSettingsObject()));
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
                EditText editor = findViewById(R.id.configfileeditor);
                editor.setText(s.JSONBeautyfier(getTestConfig()));
            }
        });

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editor = findViewById(R.id.configfileeditor);
                editor.setText(s.JSONBeautyfier(getBaseConfig()));
            }
        });

        Button readButton = findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editor = findViewById(R.id.configfileeditor);
                try {
                    editor.setText(s.JSONBeautyfier(s.getSettingsObject()));
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