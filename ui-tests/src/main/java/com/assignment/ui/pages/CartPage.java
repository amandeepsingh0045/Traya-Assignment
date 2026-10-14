package com.assignment.ui.pages;

import com.assignment.ui.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Cart page (/cart.html). */
public class CartPage extends BasePage {

    private static final By CART_ITEM_NAMES = dataTest("inventory-item-name");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");

    /** Builds an xpath scoped to the cart row containing the given product. */
    private static By fieldInRow(String productName, String dataTestField) {
        return By.xpath("//*[@data-test='inventory-item-name' and normalize-space()='" + productName + "']"
                + "/ancestor::*[@data-test='inventory-item']//*[@data-test='" + dataTestField + "']");
    }

    public boolean isLoaded() {
        return WaitUtils.urlContains(driver, "cart.html") && isDisplayed(CHECKOUT_BUTTON);
    }

    public List<String> getProductNames() {
        return WaitUtils.allVisible(driver, CART_ITEM_NAMES).stream().map(WebElement::getText).map(String::trim).toList();
    }

    public String getProductPrice(String productName) {
        return getText(fieldInRow(productName, "inventory-item-price"));
    }

    public int getProductQuantity(String productName) {
        return Integer.parseInt(getText(fieldInRow(productName, "item-quantity")));
    }

    public CheckoutInformationPage proceedToCheckout() {
        click(CHECKOUT_BUTTON, "Checkout");
        return new CheckoutInformationPage();
    }

    public ProductsPage continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON, "Continue shopping");
        return new ProductsPage();
    }
}
