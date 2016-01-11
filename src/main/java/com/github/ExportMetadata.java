package com.github;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.database.DatabaseWrapper;

public class ExportMetadata {

    private final DatabaseWrapper database;
    private final Collection<Table> tables;

    public ExportMetadata(ExportConfig config) {
        this.database = new DatabaseWrapper(config);
        this.tables = this.database.getTables();
    }

    /**
     * @return the tables
     */
    public Collection<Table> getTables() {
        return Collections.unmodifiableCollection(tables);
    }

    /**
     * Convenience method
     * 
     * @param tablename
     *            the table's name, not <code>null</code>
     * @return
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
        return database.getQueryResult();
    }
}
