package com.github.kaiwinter.dbjson.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Config {

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

    public static Config fromFile(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fromFile(fileInputStream);
        }
    }
}
