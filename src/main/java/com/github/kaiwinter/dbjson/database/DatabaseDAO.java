package com.github.kaiwinter.dbjson.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.meta.Table;
import com.google.common.base.CaseFormat;

public final class DatabaseDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDAO.class.getSimpleName());

    private final Config config;

    public DatabaseDAO(Config config) {
        this.config = config;
    }

    /**
     * @return all tables in the database sorted by table name
     */
    public List<Table> getTables() {
        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                ResultSet resultTables = connection.getMetaData().getTables(null, null, null, null);) {
            List<Table> tables = new ArrayList<>();
            while (resultTables.next()) {
                String tablename = resultTables.getString("TABLE_NAME");
                tables.add(new Table(this, tablename));
            }

            Collections.sort(tables, new Comparator<Table>() {

                @Override
                public int compare(Table o1, Table o2) {
                    return o2.getTablename().compareTo(o1.getTablename());
                }
            });
            return tables;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return all rows from the table
     */
    public QueryResult getTableData(String tablename) {
        String query = "SELECT * FROM " + tablename;
        return getQueryResultIntern(query);
    }

    /**
     * Returns the resulting rows from the query.
     * 
     * @return the result of the query which was defined in the config.json.
     */
    public QueryResult getQueryResult() {
        return getQueryResultIntern(config.query);
    }

    private QueryResult getQueryResultIntern(String query) {
        QueryResult queryResult = new QueryResult();

        try (Connection connection = DriverManager.getConnection(config.connectionString, config.user, config.password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();) {

            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnLabel = resultSet.getMetaData().getColumnLabel(i + 1);
                if (config.underscoreToCamelcase) {
                    columnLabel = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnLabel);
                    queryResult.columnLabels.add(columnLabel);
                } else {
                    queryResult.columnLabels.add(columnLabel);
                }
            }

            while (resultSet.next()) {
                Object[] data = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    data[i] = resultSet.getObject(i + 1);
                }
                queryResult.data.add(data);
            }

            return queryResult;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
