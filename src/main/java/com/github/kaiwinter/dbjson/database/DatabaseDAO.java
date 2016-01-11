package com.github.kaiwinter.dbjson.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.meta.Table;

public final class DatabaseDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDAO.class.getSimpleName());

    private final Config config;

    public DatabaseDAO(Config config) {
        this.config = config;
    }

    public Collection<Table> getTables() {
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultTables = connection.getMetaData().getTables(null, null, null, null);) {
            Collection<Table> tables = new HashSet<>();
            while (resultTables.next()) {
                String tablename = resultTables.getString("TABLE_NAME");
                tables.add(new Table(this, tablename));
            }
            return tables;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getColumnNames(String tablename) {
        List<String> columns = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultColumns = connection.getMetaData().getColumns(null, null, tablename, null)) {

            while (resultColumns.next()) {
                System.out.println();
                String columnname = resultColumns.getString("COLUMN_NAME");
                columns.add(columnname);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return columns;
    }

    /**
     * Returns all rows from the table. The first element in the list contains the column headers.
     * 
     * @return
     */
    public List<Object[]> getContent(String tablename) {
        String query = "SELECT * FROM " + tablename;
        return getQueryResultIntern(query);
    }

    /**
     * Returns the resulting rows from the query. The first element in the list contains the column headers.
     * 
     * @return the result of the query which was defined in the config.json.
     */
    public List<Object[]> getQueryResult() {
        return getQueryResultIntern(config.query);
    }

    private List<Object[]> getQueryResultIntern(String query) {
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
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
