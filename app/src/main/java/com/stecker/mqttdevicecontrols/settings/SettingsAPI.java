package com.stecker.mqttdevicecontrols.settings;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class SettingsAPI {
    private static final Type REVIEW_TYPE = new TypeToken<LinkedList<Server>>() {}.getType();
    private String filename;
    private Gson gson;
    private File f;

    public SettingsAPI(String filename) {
        this.filename = filename;
        this.f = new File(filename);
        gson = new Gson();

    }

    public void saveSettings(LinkedList<Server> servers) throws FileNotFoundException {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        saveSettings(gson.toJson(servers));
    }

    public boolean saveSettings(String settings) throws JsonSyntaxException {
        gson.fromJson(settings, Object.class);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            PrintStream ps = new PrintStream(f);
            ps.print(settings);
            ps.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public String JSONBeautyfier(LinkedList<Server> servers) {
        Gson beautygson = new GsonBuilder().setPrettyPrinting().create();
        return beautygson.toJson(servers);
    }

    public String getSettingsText() {
        String settingsText = "";
        if (f.exists() && f.canRead()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                StringBuilder sb = new StringBuilder();
                while (fis.available() > 0) {
                    sb.append((char) fis.read());
                }
                settingsText = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return settingsText;
    }

    public LinkedList<Server> getSettingsObject() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(filename));

        LinkedList<Server> servers = gson.fromJson(reader, REVIEW_TYPE);
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servers;
    }

    public String getFilename() {
        return filename;
    }
}
