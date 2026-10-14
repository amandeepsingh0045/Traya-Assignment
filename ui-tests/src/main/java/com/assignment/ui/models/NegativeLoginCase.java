package com.assignment.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One data-driven negative case loaded from JSON; toString() gives a readable report name. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NegativeLoginCase {
    private String scenario;
    private String username;
    private String password;
    private String expectedError;

    public LoginCredentials credentials() {
        return LoginCredentials.builder().username(username).password(password).build();
    }

    @Override
    public String toString() {
        return scenario;
    }
}
