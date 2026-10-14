package com.assignment.ui.pages;

import com.assignment.ui.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** Checkout step two: order summary (/checkout-step-two.html). */
public class CheckoutOverviewPage extends BasePage {

    private static final By ITEM_NAMES = dataTest("inventory-item-name");
    private static final By ITEM_TOTAL_LABEL = dataTest("subtotal-label");
    private static final By TAX_LABEL = dataTest("tax-label");
    private static final By TOTAL_LABEL = dataTest("total-label");
    private static final By FINISH_BUTTON = By.id("finish");

    public boolean isLoaded() {
        return WaitUtils.urlContains(driver, "checkout-step-two") && isDisplayed(FINISH_BUTTON);
    }

    public List<String> getProductNames() {
        return WaitUtils.allVisible(driver, ITEM_NAMES).stream().map(WebElement::getText).map(String::trim).toList();
    }

    public BigDecimal getItemTotal() { return amountFrom(ITEM_TOTAL_LABEL); }

    public BigDecimal getTax() { return amountFrom(TAX_LABEL); }

    public BigDecimal getTotal() { return amountFrom(TOTAL_LABEL); }

    /** "Item total: $29.99" -> 29.99 (BigDecimal avoids floating-point rounding issues with money). */
    private BigDecimal amountFrom(By label) {
        String text = getText(label);
        return new BigDecimal(text.substring(text.indexOf('$') + 1));
    }

    public CheckoutCompletePage finishOrder() {
        click(FINISH_BUTTON, "Finish");
        return new CheckoutCompletePage();
    }
}
