package com.assignment.ui.pages;

import com.assignment.common.config.ConfigManager;
import com.assignment.ui.models.LoginCredentials;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = dataTest("error");

    public LoginPage open() {
        String url = ConfigManager.get("ui.baseUrl");
        step("Launch application: " + url);
        driver.get(url);
        isDisplayed(LOGIN_BUTTON);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }

    /** Successful login -> returns the next page (fluent page-to-page navigation). */
    public ProductsPage loginAs(LoginCredentials credentials) {
        submit(credentials);
        return new ProductsPage();
    }

    /** Failed login -> stays on the login page so the error can be asserted. */
    public LoginPage attemptLoginAs(LoginCredentials credentials) {
        submit(credentials);
        return this;
    }

    private void submit(LoginCredentials credentials) {
        step("Login as '" + credentials.getUsername() + "'");
        type(USERNAME_INPUT, credentials.getUsername(), "Username");
        type(PASSWORD_INPUT, credentials.getPassword(), "Password");
        click(LOGIN_BUTTON, "Login");
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
