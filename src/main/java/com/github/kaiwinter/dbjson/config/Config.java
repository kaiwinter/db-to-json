package com.github.kaiwinter.dbjson.config;

import java.io.Reader;

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
    
    public static Config fromFile(Reader reader) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(reader, Config.class);
    }
}
