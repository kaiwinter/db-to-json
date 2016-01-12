package com.github.kaiwinter.dbjson.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.dbjson.database.DatabaseDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDAO.class.getSimpleName());

    public String jdbcDriver;
    public String connectionString;
    public String user;
    public String password;
    public String query;

    private Config() {
        // use fromFile
    }

    public static Config fromFile(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(reader, Config.class);
    }

    public static Config fromFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fromFile(fileInputStream);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
