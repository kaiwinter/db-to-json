package com.github.kaiwinter.dbjson.json;

import java.io.IOException;
import java.util.List;

import com.google.gson.stream.JsonWriter;

public final class JsonExporter {

    /**
     * @param writer
     *            {@link JsonWriter} to write to
     * @param headerAndTableContent
     *            List which first element contains the column header names. Following elements are the table rows
     * @param tablename
     *            the name which is used in the JSON as key for this node
     * @throws IOException
     *             if writing by the {@link JsonWriter} fails
     */
    public void writeList(JsonWriter writer, List<Object[]> headerAndTableContent, String tablename)
            throws IOException {
        Object[] header = headerAndTableContent.get(0);

        writer.beginObject();
        writer.name(tablename);

        writer.beginArray();

        for (int row = 1; row < headerAndTableContent.size(); row++) {
            writer.beginObject();
            Object[] rowData = headerAndTableContent.get(row);
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

}
