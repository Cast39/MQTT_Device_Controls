package com.stecker.mqttdevicecontrols.settings;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
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
    public void saveSettings(String settings) throws FileNotFoundException {
        PrintStream ps = new PrintStream(f);
        ps.print(settings);
        ps.close();
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
