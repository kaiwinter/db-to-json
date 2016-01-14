package com.github.kaiwinter.dbjson.json;

import java.io.IOException;
import java.util.List;

import com.github.kaiwinter.dbjson.database.QueryResult;
import com.google.gson.stream.JsonWriter;

public final class JsonExporter {

    /**
     * @param writer
     *            {@link JsonWriter} to write to
     * @param queryResult
     *            the result of a database query
     * @param tablename
     *            the name which is used in the JSON as key for this node
     * @throws IOException
     *             if writing by the {@link JsonWriter} fails
     */
    public void writeList(JsonWriter writer, QueryResult queryResult, String tablename) throws IOException {
        List<String> header = queryResult.columnLabels;

        writer.beginObject();
        writer.name(tablename);

        writer.beginArray();

        for (Object[] rowData : queryResult.data) {
            writer.beginObject();
            for (int column = 0; column < rowData.length; column++) {
                Object columnValue = rowData[column];
                if (columnValue == null) {
                    writer.name(header.get(column)).nullValue();
                } else if (columnValue instanceof String) {
                    writer.name(header.get(column)).value((String) columnValue);
                } else if (columnValue instanceof Number) {
                    writer.name(header.get(column)).value((Number) columnValue);
                } else {
                    throw new IllegalArgumentException("Unknown type: " + columnValue.getClass());
                }
            }
            writer.endObject();
        }

        writer.endArray();
        writer.endObject();
    }
}
