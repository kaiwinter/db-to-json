package com.github;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ExportConfig {
    public String jdbcDriver;
    public String connectionString;
    public String user;
    public String password;
    public String query;

    private ExportConfig() {
        // use fromFile
    }
    
    public static ExportConfig fromFile(Reader reader) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(reader, ExportConfig.class);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

}
