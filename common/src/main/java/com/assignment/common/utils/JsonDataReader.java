package com.assignment.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Reads test data JSON files from the classpath (src/test/resources/testdata). */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() { }

    public static JsonNode read(String classpathFile) {
        try (InputStream in = JsonDataReader.class.getClassLoader().getResourceAsStream(classpathFile)) {
            if (in == null) throw new IllegalArgumentException("Test data not found: " + classpathFile);
            return MAPPER.readTree(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data " + classpathFile, e);
        }
    }

    public static <T> T read(String classpathFile, Class<T> type) {
        try (InputStream in = JsonDataReader.class.getClassLoader().getResourceAsStream(classpathFile)) {
            if (in == null) throw new IllegalArgumentException("Test data not found: " + classpathFile);
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data " + classpathFile, e);
        }
    }

    /** Reads the array at {@code arrayField} and maps every element to {@code type}. */
    public static <T> java.util.List<T> readList(String classpathFile, String arrayField, Class<T> type) {
        JsonNode array = read(classpathFile).get(arrayField);
        if (array == null || !array.isArray()) {
            throw new IllegalArgumentException("No array '" + arrayField + "' in " + classpathFile);
        }
        java.util.List<T> items = new java.util.ArrayList<>();
        array.forEach(node -> items.add(MAPPER.convertValue(node, type)));
        return items;
    }

    /** Converts a list into the Object[][] shape TestNG data providers expect (one arg per row). */
    public static Object[][] toDataProvider(java.util.List<?> items) {
        return items.stream().map(i -> new Object[]{i}).toArray(Object[][]::new);
    }
}
