package com.assignment.ui.pages;

import com.assignment.ui.components.HeaderComponent;
import com.assignment.ui.utils.WaitUtils;
import org.openqa.selenium.By;

/** Order confirmation (/checkout-complete.html). */
public class CheckoutCompletePage extends BasePage {

    private static final By CONFIRMATION_HEADER = dataTest("complete-header");
    private static final By CONFIRMATION_TEXT = dataTest("complete-text");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");

    private final HeaderComponent header = new HeaderComponent();

    public HeaderComponent header() {
        return header;
    }

    public boolean isLoaded() {
        return WaitUtils.urlContains(driver, "checkout-complete") && isDisplayed(CONFIRMATION_HEADER);
    }

    public String getConfirmationHeader() {
        return getText(CONFIRMATION_HEADER);
    }

    public String getConfirmationText() {
        return getText(CONFIRMATION_TEXT);
    }

    public ProductsPage backHome() {
        click(BACK_HOME_BUTTON, "Back Home");
        return new ProductsPage();
    }
}
