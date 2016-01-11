package com.github;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.stream.JsonWriter;

public class ExporterTest {

    @Test
    public void testSqliteMetadataTables() throws SQLException {

        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));
        ExportMetadata metadata = new ExportMetadata(config);
        Collection<Table> tables = metadata.getTables();
        Assert.assertEquals(2, tables.size());

        int matches = 0;
        for (Table table : tables) {
            if (table.getTablename().equals("user")) {
                matches++;
            } else if (table.getTablename().equals("invoice")) {
                matches++;
            }
        }
        Assert.assertEquals(2, matches);
    }

    @Test
    public void testSqliteData() throws SQLException {
        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));
        ExportMetadata metadata = new ExportMetadata(config);

        Table table = metadata.getTable("user");
        List<Object[]> tableContent = table.getContent();
        Assert.assertEquals(3, tableContent.size());

        Assert.assertEquals("id", tableContent.get(0)[0]);
        Assert.assertEquals("username", tableContent.get(0)[1]);

        Assert.assertEquals(1, tableContent.get(1)[0]);
        Assert.assertEquals("User A", tableContent.get(1)[1]);

        Assert.assertEquals(2, tableContent.get(2)[0]);
        Assert.assertEquals("User B", tableContent.get(2)[1]);
    }

    @Test
    public void testSqliteWriteJsonOne() throws SQLException, IOException {
        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));

        ExportMetadata metadata = new ExportMetadata(config);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            Table table = metadata.getTable("invoice");
            table.export(writer);
        }

        Assert.assertEquals("{\"invoice\":[{\"id\":1,\"address\":\"Address A\"},{\"id\":2,\"address\":\"Address B\"}]}",
                stringWriter.getBuffer().toString());
    }

    @Test
    public void testSqliteWriteJsonMultiple() throws SQLException, IOException {
        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));

        ExportMetadata metadata = new ExportMetadata(config);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            writer.beginArray();
            List<Table> tables = new ArrayList<>(metadata.getTables());
            // Sort for a stable test result
            Collections.sort(tables, new Comparator<Table>() {

                @Override
                public int compare(Table o1, Table o2) {
                    return o2.getTablename().compareTo(o1.getTablename());
                }
            });
            for (Table table : tables) {
                table.export(writer);
            }
            writer.endArray();
        }

        Assert.assertEquals(
                "[{\"user\":[{\"id\":1,\"username\":\"User A\"},{\"id\":2,\"username\":\"User B\"}]},{\"invoice\":[{\"id\":1,\"address\":\"Address A\"},{\"id\":2,\"address\":\"Address B\"}]}]",
                stringWriter.getBuffer().toString());
    }

    @Test
    public void testSqliteQueryResultSingleColumn() throws SQLException {
        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));

        ExportMetadata metadata = new ExportMetadata(config);
        List<Object[]> queryResult = metadata.getQueryResult();
        Assert.assertEquals(2, queryResult.size());
        Assert.assertEquals(1, queryResult.iterator().next().length);
        Assert.assertEquals("id", queryResult.get(0)[0]);
        Assert.assertEquals(1, queryResult.get(1)[0]);
    }

    @Test
    public void testSqliteQueryResultTwoColumnsTwoRows() throws SQLException {
        ExportConfig config = ExportConfig.fromFile(new InputStreamReader(
                ExporterTest.class.getResourceAsStream("sqlite/config-multiplecolumnquery.json")));

        ExportMetadata metadata = new ExportMetadata(config);
        List<Object[]> queryResult = metadata.getQueryResult();
        Assert.assertEquals(3, queryResult.size());
        Assert.assertEquals(1, queryResult.iterator().next().length);
        Assert.assertEquals("username", queryResult.get(0)[0]);
        Assert.assertEquals("User A", queryResult.get(1)[0]);
        Assert.assertEquals("User B", queryResult.get(2)[0]);
    }

    @Test
    public void testSqliteMetadataTableColumns() throws SQLException {
        ExportConfig config = ExportConfig
                .fromFile(new InputStreamReader(ExporterTest.class.getResourceAsStream("sqlite/config.json")));

        ExportMetadata metadata = new ExportMetadata(config);
        Table table = metadata.getTable("user");
        List<String> columnNames = table.getColumnNames();
        Assert.assertEquals("id", columnNames.get(0));
        Assert.assertEquals("username", columnNames.get(1));
    }
}
