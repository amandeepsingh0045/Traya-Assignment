package com.assignment.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String firstName;
    private String lastName;
    private String postalCode;

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + postalCode + ")";
    }
}
