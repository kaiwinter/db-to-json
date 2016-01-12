package com.github.kaiwinter.dbjson.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.database.DatabaseDAO;

public final class Database {

    private final DatabaseDAO database;
    private final Collection<Table> tables;

    /**
     * Constructs a new {@link Database} and loads the table metadata from the database.
     * 
     * @param config
     */
    public Database(Config config) {
        this.database = new DatabaseDAO(config);
        this.tables = this.database.getTables();
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

    /**
     * Returns the resulting rows from the query. The first element in the list contains the column headers.
     * 
     * @return the result of the query which was defined in the config.json.
     */
    public List<Object[]> getQueryResult() {
        return database.getQueryResultWithHeader();
    }
}
