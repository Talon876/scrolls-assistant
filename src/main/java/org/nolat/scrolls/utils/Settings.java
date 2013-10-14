package org.nolat.scrolls.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class Settings {

    private static final Logger log = Logger.getLogger(Settings.class);
    private String jsonData;
    private final ScrollsSettings settings;

    /**
     * Loads settings from file.
     * 
     * @param path
     *            path to the settings json file
     */
    public Settings(String path) {
        log.info("Loading settings from " + path);
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            try {
                jsonData = FileReader.readFile(path);
            } catch (IOException ex) {
                log.error("Could not read settings file", ex);
            }
        } else {
            log.error("Settings file does not exist: " + path);
        }
        Gson gson = new Gson();
        settings = gson.fromJson(jsonData, ScrollsSettings.class);
    }

    public String getEncryptedEmail() {
        return settings.encryptedEmail;
    }

    public String getEncryptedPassword() {
        return settings.encryptedPassword;
    }

    private class ScrollsSettings {
        public String encryptedEmail;
        public String encryptedPassword;
    }
}
