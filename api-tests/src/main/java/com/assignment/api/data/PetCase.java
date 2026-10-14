package com.assignment.api.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row of a data-driven positive pet test loaded from testdata/api/pets.json. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetCase {
    private String name;
    private String category;
    private String status;
    private java.util.List<String> tags;

    @Override
    public String toString() { return name + " [" + status + "]"; }
}
