package com.github.kaiwinter.dbjson.meta;

import java.io.IOException;
import java.util.List;

import com.github.kaiwinter.dbjson.database.DatabaseDAO;
import com.google.gson.stream.JsonWriter;

public final class Table {

    private final String tablename;
    private final DatabaseDAO database;

    public Table(DatabaseDAO databaseWrapper, String tablename) {
        this.database = databaseWrapper;
        this.tablename = tablename;
    }

    public List<String> getColumnNames() {
        return database.getColumnNames(tablename);
    }

    /**
     * Returns all rows from the table. The first element in the list contains the column headers.
     * 
     * @return
     */
    public List<Object[]> getContent() {
        return database.getContent(tablename);
    }

    public void export(JsonWriter writer) throws IOException {
        export(writer, false);
    }

    public void export(JsonWriter writer, boolean pretty) throws IOException {
        if (pretty) {
            writer.setIndent("    ");
        }

        List<Object[]> tableContent = getContent();
        Object[] header = tableContent.get(0);

        writer.beginObject();
        writer.name(tablename);

        writer.beginArray();

        for (int row = 1; row < tableContent.size(); row++) {
            writer.beginObject();
            Object[] rowData = tableContent.get(row);
            for (int column = 0; column < rowData.length; column++) {
                Object columnValue = rowData[column];
                if (columnValue == null) {
                    writer.name((String) header[column]).nullValue();
                } else if (columnValue instanceof String) {
                    writer.name((String) header[column]).value((String) columnValue);
                } else if (columnValue instanceof Number) {
                    writer.name((String) header[column]).value((Number) columnValue);
                } else {
                    throw new IllegalArgumentException("Unknown type: " + columnValue.getClass());
                }
            }
            writer.endObject();
        }

        writer.endArray();
        writer.endObject();
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }
}
