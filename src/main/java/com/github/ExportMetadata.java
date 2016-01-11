package com.github;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ExportMetadata {
    private final ExportConfig config;

    private final Collection<Table> tables;

    public ExportMetadata(ExportConfig config) throws SQLException {
        this.config = config;

        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultTables = connection.getMetaData().getTables(null, null, null, null);) {

            tables = new HashSet<>();
            while (resultTables.next()) {
                String tablename = resultTables.getString("TABLE_NAME");
                tables.add(new Table(this, tablename));
            }

        }
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
     * @return the config
     */
    public ExportConfig getConfig() {
        return config;
    }

    /**
     * @return the tables
     */
    public Collection<Table> getTables() {
        return tables;
    }

    /**
     * Returns the resulting rows from the query. The first element in the list contains the column headers.
     * 
     * @return the result of the query which was defined in the config.json.
     * @throws SQLException
     */
    public List<Object[]> getQueryResult() throws SQLException {
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                PreparedStatement preparedStatement = connection.prepareStatement(config.query);
                ResultSet resultSet = preparedStatement.executeQuery();) {

            List<Object[]> tableContent = new ArrayList<>();

            int columnCount = resultSet.getMetaData().getColumnCount();
            Object[] header = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                header[i] = resultSet.getMetaData().getColumnLabel(i + 1);
            }
            tableContent.add(header);

            while (resultSet.next()) {
                Object[] data = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    data[i] = resultSet.getObject(i + 1);
                }
                tableContent.add(data);
            }

            return tableContent;
        }
    }

}
