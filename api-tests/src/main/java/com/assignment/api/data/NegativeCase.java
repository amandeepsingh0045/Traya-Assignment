package com.assignment.api.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row of a data-driven negative test loaded from JSON.
 * {@code payload} is kept as raw JSON so we can send shapes a POJO can't represent
 * (missing fields, wrong types, unknown enum values).
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NegativeCase {
    private String name;
    private JsonNode payload;
    private int expectedStatus;
    private String expectedMessage;

    @Override
    public String toString() { return name; }   // readable name in TestNG / Extent output
}
