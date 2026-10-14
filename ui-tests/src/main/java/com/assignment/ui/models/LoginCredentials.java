package com.assignment.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginCredentials {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "user='" + username + "'";   // never print the password into reports
    }
}
