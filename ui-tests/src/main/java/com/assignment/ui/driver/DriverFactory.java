package com.assignment.ui.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Map;

/**
 * FACTORY PATTERN: the only place that knows how to build a browser.
 * Callers ask for a {@link BrowserType}; they never call "new ChromeDriver()" themselves.
 * Adding a new browser (or RemoteWebDriver for a Grid) = one new case here, no test changes.
 * Selenium Manager (built into Selenium 4.6+) downloads the matching driver binary automatically.
 */
public final class DriverFactory {

    private DriverFactory() { }

    public static WebDriver createDriver(BrowserType browser, boolean headless) {
        return switch (browser) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
            case EDGE -> new EdgeDriver(edgeOptions(headless));
            case SAFARI -> new SafariDriver();   // Safari has no headless mode
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080", "--disable-notifications", "--guest");
        // Suppress Chrome's "change your password" leak-detection popup, which blocks SauceDemo flows
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        options.addArguments("--width=1920", "--height=1080");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        return options;
    }
}
