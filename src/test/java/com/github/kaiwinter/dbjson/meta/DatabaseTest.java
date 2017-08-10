package com.github.kaiwinter.dbjson.meta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.kaiwinter.dbjson.config.Config;

public final class DatabaseTest {

    @Test
    public void testSqliteWriteJsonAllTables() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));

        Database metadata = new Database(config);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        metadata.exportAllTables(stream);
        Assert.assertEquals(
                "[{\"user\":[{\"id\":1,\"username\":\"User A\"},{\"id\":2,\"username\":\"User B\"}]},{\"invoice\":[{\"id\":1,\"address_home\":\"Address A\"},{\"id\":2,\"address_home\":\"Address B\"}]}]",
                stream.toString());
    }

    @Test
    public void testSqliteQueryResultSingleColumn() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config.json"));

        Database metadata = new Database(config);
        OutputStream stream = new ByteArrayOutputStream();
        metadata.exportQueryResult(stream);
        Assert.assertEquals("{\"SELECT id FROM user WHERE id=1\":[{\"id\":1}]}", stream.toString());
    }

    @Test
    public void testSqliteQueryResultTwoColumnsTwoRows() throws IOException {
        Config config = Config.fromFile(TableTest.class.getResourceAsStream("sqlite/config-multiplecolumnquery.json"));

        Database metadata = new Database(config);
        OutputStream stream = new ByteArrayOutputStream();
        metadata.exportQueryResult(stream);
        Assert.assertEquals(
                "{\"SELECT username FROM user WHERE id<10\":[{\"username\":\"User A\"},{\"username\":\"User B\"}]}",
                stream.toString());
    }
}
