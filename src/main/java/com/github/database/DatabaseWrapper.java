package com.github.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.github.ExportConfig;
import com.github.Table;

public class DatabaseWrapper {

    private ExportConfig config;

    public DatabaseWrapper(ExportConfig config) {
        this.config = config;
    }

    public Collection<Table> getTables() throws SQLException {
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultTables = connection.getMetaData().getTables(null, null, null, null);) {
            Collection<Table> tables = new HashSet<>();
            while (resultTables.next()) {
                String tablename = resultTables.getString("TABLE_NAME");
                tables.add(new Table(this, tablename));
            }
            return tables;
        }
    }

    public List<String> getColumnNames(String tablename) throws SQLException {
        List<String> columns = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultColumns = connection.getMetaData().getColumns(null, null, tablename, null)) {

            while (resultColumns.next()) {
                System.out.println();
                String columnname = resultColumns.getString("COLUMN_NAME");
                columns.add(columnname);
            }
        }

        return columns;
    }

    /**
     * Returns all rows from the table. The first element in the list contains the column headers.
     * 
     * @return
     * @throws SQLException
     */
    public List<Object[]> getContent(String tablename) throws SQLException {
        String query = "SELECT * FROM " + tablename;
        return getQueryResultIntern(query);
    }

    /**
     * Returns the resulting rows from the query. The first element in the list contains the column headers.
     * 
     * @return the result of the query which was defined in the config.json.
     * @throws SQLException
     */
    public List<Object[]> getQueryResult() throws SQLException {
        return getQueryResultIntern(config.query);
    }

    private List<Object[]> getQueryResultIntern(String query) throws SQLException {
        List<Object[]> tableContent = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();) {

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
