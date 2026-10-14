package com.assignment.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Checkout form validation case loaded from JSON. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutValidationCase {
    private String scenario;
    private Customer customer;
    private String expectedError;

    @Override
    public String toString() {
        return scenario;
    }
}
