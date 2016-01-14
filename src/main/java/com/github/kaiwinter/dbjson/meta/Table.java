package com.github.kaiwinter.dbjson.meta;

import java.io.IOException;

import com.github.kaiwinter.dbjson.database.DatabaseDAO;
import com.github.kaiwinter.dbjson.database.QueryResult;
import com.github.kaiwinter.dbjson.json.JsonExporter;
import com.google.gson.stream.JsonWriter;

public final class Table {

    private final String tablename;
    private final DatabaseDAO databaseDAO;
    private final JsonExporter json;

    public Table(DatabaseDAO databaseDAO, String tablename) {
        this.databaseDAO = databaseDAO;
        this.tablename = tablename;
        this.json = new JsonExporter();
    }

    /**
     * Returns all rows from the table.
     * 
     * @return
     */
    public QueryResult getTableData() {
        return databaseDAO.getTableData(tablename);
    }

    public void exportAllRows(JsonWriter writer) throws IOException {
        exportAllRows(writer, false);
    }

    public void exportAllRows(JsonWriter writer, boolean pretty) throws IOException {
        if (pretty) {
            writer.setIndent("    ");
        }

        QueryResult queryResult = getTableData();
        json.writeList(writer, queryResult, tablename);
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }
}
