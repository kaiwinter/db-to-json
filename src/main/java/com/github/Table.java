package com.github;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonWriter;

public class Table {
    private final ExportMetadata exportMetadata;
    private final String tablename;

    public Table(ExportMetadata exportMetadata, String tablename) {
        this.exportMetadata = exportMetadata;
        this.tablename = tablename;
    }

    public List<String> getColumnNames() throws SQLException {
        ExportConfig config = exportMetadata.getConfig();
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
    public List<Object[]> getContent() throws SQLException {
        ExportConfig config = exportMetadata.getConfig();
        String query = "SELECT * FROM " + tablename;
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

    public void export(JsonWriter writer) throws SQLException, IOException {
        export(writer, false);
    }

    public void export(JsonWriter writer, boolean pretty) throws SQLException, IOException {
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
