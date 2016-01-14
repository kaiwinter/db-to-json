package com.github.kaiwinter.dbjson.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the column labels and data from a database query.
 */
public final class QueryResult {
    public final List<String> columnLabels = new ArrayList<>();;
    public final List<Object[]> data = new ArrayList<>();;
}