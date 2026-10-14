package com.assignment.ui.pages;

import com.assignment.common.reporting.ExtentManager;
import com.assignment.ui.driver.DriverManager;
import com.assignment.ui.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Parent of every page object. Holds the thread's driver and provides waited, logged actions,
 * so individual pages contain only locators + business methods.
 *
 * Locator strategy (in order of preference):
 *   1. By.id            - SauceDemo exposes ids for all inputs/buttons
 *   2. By.xpath on the  data-test attribute - added by devs specifically for automation,
 *                        unaffected by styling / layout changes
 *   Never: absolute xpaths, index-based xpaths, or CSS class names (these change with styling).
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final WebDriver driver;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
    }

    // ---------- locator helpers ----------

    /** Stable xpath on the data-test attribute, e.g. //*[@data-test='title'] */
    protected static By dataTest(String value) {
        return By.xpath("//*[@data-test='" + value + "']");
    }

    // ---------- actions ----------

    protected void click(By locator, String elementName) {
        step("Click on '" + elementName + "'");
        WaitUtils.clickable(driver, locator).click();
    }

    protected void type(By locator, String text, String elementName) {
        step("Enter '" + text + "' in '" + elementName + "'");
        WebElement element = WaitUtils.visible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return WaitUtils.visible(driver, locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return WaitUtils.visible(driver, locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Logs a business-readable step to Log4j and to the Extent report. */
    protected void step(String message) {
        log.info(message);
        ExtentManager.info(message);
    }
}
