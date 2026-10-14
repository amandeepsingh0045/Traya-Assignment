package com.assignment.ui.pages;

import com.assignment.ui.models.Customer;
import com.assignment.ui.utils.WaitUtils;
import org.openqa.selenium.By;

/** Checkout step one: customer / shipping information (/checkout-step-one.html). */
public class CheckoutInformationPage extends BasePage {

    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By ERROR_MESSAGE = dataTest("error");

    public boolean isLoaded() {
        return WaitUtils.urlContains(driver, "checkout-step-one") && isDisplayed(CONTINUE_BUTTON);
    }

    public CheckoutInformationPage enterCustomerDetails(Customer customer) {
        step("Enter shipping details for " + customer);
        type(FIRST_NAME_INPUT, customer.getFirstName(), "First Name");
        type(LAST_NAME_INPUT, customer.getLastName(), "Last Name");
        type(POSTAL_CODE_INPUT, customer.getPostalCode(), "Postal Code");
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        click(CONTINUE_BUTTON, "Continue");
        return new CheckoutOverviewPage();
    }

    /** Use when validation is expected to block navigation. */
    public CheckoutInformationPage continueExpectingError() {
        click(CONTINUE_BUTTON, "Continue");
        return this;
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public CartPage cancel() {
        click(CANCEL_BUTTON, "Cancel");
        return new CartPage();
    }
}
