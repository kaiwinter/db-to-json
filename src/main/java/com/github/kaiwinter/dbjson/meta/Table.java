package com.github.kaiwinter.dbjson.meta;

import java.io.IOException;
import java.util.List;

import com.github.kaiwinter.dbjson.database.DatabaseDAO;
import com.github.kaiwinter.dbjson.json.JsonExporter;
import com.google.gson.stream.JsonWriter;

public final class Table {

    private final String tablename;
    private final DatabaseDAO database;
    private final JsonExporter json;

    public Table(DatabaseDAO databaseWrapper, String tablename) {
        this.database = databaseWrapper;
        this.tablename = tablename;
        this.json = new JsonExporter();
    }

    public List<String> getColumnNames() {
        return database.getColumnNames(tablename);
    }

    /**
     * Returns all rows from the table. The first element in the list contains the column headers.
     * 
     * @return
     */
    public List<Object[]> getAllWithHeader() {
        return database.getAllWithHeader(tablename);
    }

    public void exportAllRows(JsonWriter writer) throws IOException {
        exportAllRows(writer, false);
    }

    public void exportAllRows(JsonWriter writer, boolean pretty) throws IOException {
        if (pretty) {
            writer.setIndent("    ");
        }

        List<Object[]> headerAndtableContent = getAllWithHeader();
        json.writeList(writer, headerAndtableContent, tablename);
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }
}
