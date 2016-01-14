package com.github.kaiwinter.dbjson;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

public final class MainTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Test
    public void testNoParameter() {
        Main.main(new String[]{});

        Assert.assertEquals(
                "Option \"-config\" is required\n -config WERT  : the json configuration\n -outfile WERT : file to write the output to. If not set result is written to\n                 standart out\n",
                systemErrRule.getLogWithNormalizedLineSeparator());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigNotFound() {
        Main.main(new String[]{"-config", "config.json"});
    }

    @Test
    public void testExportAllTables() {
        Main.main(new String[]{"-config", "target/test-classes/com/github/kaiwinter/dbjson/meta/sqlite/config.json"});

        Assert.assertEquals("{\"SELECT id FROM user WHERE id=1\":[{\"id\":1}]}", systemOutRule.getLog());
    }

    @Test
    public void testExortQueryResults() {
        Main.main(
                new String[]{"-config", "target/test-classes/com/github/kaiwinter/dbjson/meta/sqlite/config-noquery.json"});

        Assert.assertEquals(
                "[{\"user\":[{\"id\":1,\"username\":\"User A\"},{\"id\":2,\"username\":\"User B\"}]},{\"invoice\":[{\"id\":1,\"address\":\"Address A\"},{\"id\":2,\"address\":\"Address B\"}]}]",
                systemOutRule.getLog());
    }
}
