package com.assignment.ui.base;

import com.assignment.common.config.ConfigManager;
import com.assignment.common.reporting.ScreenshotCapable;
import com.assignment.ui.driver.DriverManager;
import com.assignment.ui.models.LoginCredentials;
import com.assignment.ui.pages.LoginPage;
import com.assignment.ui.pages.ProductsPage;
import com.assignment.ui.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Browser per test method: every test is fully independent and can run alone, in any order,
 * or in parallel (thread count is set in the TestNG suite XML, not in code).
 *
 * Failure screenshots: TestListener.onTestFailure runs BEFORE @AfterMethod, so the browser
 * is still open when {@link #captureScreenshotBase64()} is called.
 */
public abstract class BaseTest implements ScreenshotCapable {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverManager.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result) {
        log.info("Finished '{}' with status {}", result.getMethod().getMethodName(),
                result.isSuccess() ? "PASS" : "FAIL/SKIP");
        DriverManager.quitDriver();
    }

    // ---------- reusable preconditions ----------

    protected LoginPage openApplication() {
        return new LoginPage().open();
    }

    protected LoginCredentials defaultUser() {
        return LoginCredentials.builder()
                .username(ConfigManager.get("ui.username"))
                .password(ConfigManager.get("ui.password"))
                .build();
    }

    /** Precondition used by most tests: app open + logged in as the configured user. */
    protected ProductsPage loginAsDefaultUser() {
        return openApplication().loginAs(defaultUser());
    }

    @Override
    public String captureScreenshotBase64() {
        if (!DriverManager.hasDriver()) return null;
        ScreenshotUtils.saveToFile(DriverManager.getDriver(), getClass().getSimpleName());
        return ScreenshotUtils.asBase64(DriverManager.getDriver());
    }
}
