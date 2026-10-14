package com.assignment.ui.utils;

import com.assignment.common.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Explicit-wait helpers. The framework uses NO implicit waits and NO Thread.sleep:
 * every interaction waits for the exact condition it needs (visible / clickable / URL).
 * Stale elements are ignored while polling, so re-rendered DOM nodes are re-located automatically.
 */
public final class WaitUtils {

    private WaitUtils() { }

    private static WebDriverWait waitFor(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("explicit.wait", 15)));
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return waitFor(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return waitFor(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static List<WebElement> allVisible(WebDriver driver, By locator) {
        return waitFor(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean urlContains(WebDriver driver, String fragment) {
        return waitFor(driver).until(ExpectedConditions.urlContains(fragment));
    }

    public static boolean textPresent(WebDriver driver, By locator, String text) {
        return waitFor(driver).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
}
