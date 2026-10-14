package com.assignment.ui.driver;

import com.assignment.common.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Holds one WebDriver per thread (ThreadLocal) so TestNG can run tests in parallel
 * without browsers being shared between threads.
 *
 * Lifecycle (driven by BaseTest):
 *   @BeforeMethod -> DriverManager.initDriver()  -> DriverFactory builds the browser
 *   test         -> DriverManager.getDriver()   -> page objects use this thread's driver
 *   @AfterMethod  -> DriverManager.quitDriver()  -> browser closed + ThreadLocal cleared (no leaks)
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() { }

    public static void initDriver() {
        BrowserType browser = BrowserType.from(ConfigManager.get("browser", "chrome"));
        boolean headless = ConfigManager.getBoolean("headless", false);
        LOG.info("[{}] Launching {} (headless={})", Thread.currentThread().getName(), browser, headless);

        WebDriver driver = DriverFactory.createDriver(browser, headless);
        driver.manage().deleteAllCookies();
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialised for thread " + Thread.currentThread().getName());
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                LOG.info("[{}] Browser closed", Thread.currentThread().getName());
            } catch (Exception e) {
                LOG.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                DRIVER.remove();   // always clear, otherwise pooled threads keep a dead driver
            }
        }
    }
}
