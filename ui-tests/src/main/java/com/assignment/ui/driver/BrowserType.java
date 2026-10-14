package com.assignment.ui.driver;

import java.util.Arrays;

public enum BrowserType {
    CHROME, FIREFOX, EDGE, SAFARI;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported browser '" + value + "'. Supported: " + Arrays.toString(values())));
    }
}
