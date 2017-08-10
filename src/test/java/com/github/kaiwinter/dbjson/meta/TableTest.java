package com.github.kaiwinter.dbjson.meta;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.database.QueryResult;
import com.google.gson.stream.JsonWriter;

public final class TableTest {

    @Test
    public void testSqliteMetadataTables() {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));
        Database metadata = new Database(config);
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
    public void testSqliteData() {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));
        Database metadata = new Database(config);
        Table table = metadata.getTable("user");

        QueryResult tableContent = table.getTableData();
        Assert.assertEquals(2, tableContent.columnLabels.size());
        Assert.assertEquals(2, tableContent.data.size());

        Assert.assertEquals("id", tableContent.columnLabels.get(0));
        Assert.assertEquals("username", tableContent.columnLabels.get(1));

        Assert.assertEquals(1, tableContent.data.get(0)[0]);
        Assert.assertEquals("User A", tableContent.data.get(0)[1]);

        Assert.assertEquals(2, tableContent.data.get(1)[0]);
        Assert.assertEquals("User B", tableContent.data.get(1)[1]);
    }

    @Test
    public void testSqliteWriteJsonOneTable() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));
        Database metadata = new Database(config);
        Table table = metadata.getTable("invoice");

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            table.exportAllRows(writer);
        }

        Assert.assertEquals(
                "{\"invoice\":[{\"id\":1,\"address_home\":\"Address A\"},{\"id\":2,\"address_home\":\"Address B\"}]}",
                stringWriter.getBuffer().toString());
    }

    @Test
    public void testSqliteWriteJsonAllTablesManually() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));
        Database metadata = new Database(config);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            writer.beginArray();
            List<Table> tables = new ArrayList<>(metadata.getTables());
            for (Table table : tables) {
                table.exportAllRows(writer);
            }
            writer.endArray();
        }

        Assert.assertEquals(
                "[{\"user\":[{\"id\":1,\"username\":\"User A\"},{\"id\":2,\"username\":\"User B\"}]},{\"invoice\":[{\"id\":1,\"address_home\":\"Address A\"},{\"id\":2,\"address_home\":\"Address B\"}]}]",
                stringWriter.getBuffer().toString());
    }

    @Test
    public void testSqliteWriteUnderscoreToCamelCase() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config-camelcase.json"));
        Database metadata = new Database(config);
        Table table = metadata.getTable("invoice");

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            table.exportAllRows(writer);
        }

        Assert.assertEquals(
                "{\"invoice\":[{\"id\":1,\"addressHome\":\"Address A\"},{\"id\":2,\"addressHome\":\"Address B\"}]}",
                stringWriter.getBuffer().toString());
    }

}
