package com.github.kaiwinter.dbjson.meta;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.database.DatabaseDAO;
import com.github.kaiwinter.dbjson.database.QueryResult;
import com.github.kaiwinter.dbjson.json.JsonExporter;
import com.google.gson.stream.JsonWriter;

public final class Database {

    private final Config config;
    private final DatabaseDAO databaseDAO;
    private final Collection<Table> tables;
    private final JsonExporter json;

    /**
     * Constructs a new {@link Database} and loads the table metadata from the database.
     * 
     * @param config
     */
    public Database(Config config) {
        this.config = config;
        this.databaseDAO = new DatabaseDAO(config);
        this.tables = this.databaseDAO.getTables();
        this.json = new JsonExporter();
    }

    /**
     * @return the tables
     */
    public Collection<Table> getTables() {
        return Collections.unmodifiableCollection(tables);
    }

    /**
     * Convenience method to get a {@link Table} from the list of tables.
     * 
     * @param tablename
     *            the table's name, not <code>null</code>
     * @return the {@link Table} object, or <code>null</code>
     */
    public Table getTable(String tablename) {
        Objects.requireNonNull(tablename, "tablename must not be null");

        for (Table table : tables) {
            if (tablename.equals(table.getTablename())) {
                return table;
            }
        }
        return null;
    }

    public void exportQueryResult(OutputStream stream) throws IOException {
        exportQueryResult(stream, false);
    }

    public void exportQueryResult(OutputStream stream, boolean pretty) throws IOException {
        QueryResult queryResult = databaseDAO.getQueryResult();

        try (JsonWriter writer = new JsonWriter(new PrintWriter(stream))) {
            if (pretty) {
                writer.setIndent("   ");
            }
            json.writeList(writer, queryResult, config.query);
        }
    }

    /**
     * @see #exportAllTables(OutputStream, boolean)
     */
    public void exportAllTables(OutputStream stream) throws IOException {
        exportAllTables(stream, false);
    }

    /**
     * Exports all tables into one JSON file. In contrast to {@link Table#exportAllRows(JsonWriter)} this can use a
     * {@link OutputStream}. Table exports needs to be in a JsonWriter-context to add additional nodes to the JSON.
     * 
     * @param stream
     * @param pretty
     * @throws IOException
     */
    public void exportAllTables(OutputStream stream, boolean pretty) throws IOException {
        try (JsonWriter writer = new JsonWriter(new PrintWriter(stream))) {
            if (pretty) {
                writer.setIndent("   ");
            }
            writer.beginArray();
            for (Table table : getTables()) {
                table.exportAllRows(writer);
            }
            writer.endArray();
        }
    }
}
