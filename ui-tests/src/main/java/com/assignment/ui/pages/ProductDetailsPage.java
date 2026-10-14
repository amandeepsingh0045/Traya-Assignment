package com.assignment.ui.pages;

import com.assignment.ui.components.HeaderComponent;
import org.openqa.selenium.By;

/** Single product page (/inventory-item.html?id=N). */
public class ProductDetailsPage extends BasePage {

    private static final By PRODUCT_NAME = dataTest("inventory-item-name");
    private static final By PRODUCT_PRICE = dataTest("inventory-item-price");
    private static final By PRODUCT_DESCRIPTION = dataTest("inventory-item-desc");
    private static final By ADD_TO_CART_BUTTON = By.id("add-to-cart");
    private static final By REMOVE_BUTTON = By.id("remove");
    private static final By BACK_TO_PRODUCTS = By.id("back-to-products");

    private final HeaderComponent header = new HeaderComponent();

    public HeaderComponent header() {
        return header;
    }

    public String getProductName() {
        return getText(PRODUCT_NAME);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    public String getProductDescription() {
        return getText(PRODUCT_DESCRIPTION);
    }

    public ProductDetailsPage addToCart() {
        click(ADD_TO_CART_BUTTON, "Add to cart");
        isDisplayed(REMOVE_BUTTON);    // wait until UI confirms the item was added
        return this;
    }

    public boolean isRemoveButtonDisplayed() {
        return isDisplayed(REMOVE_BUTTON);
    }

    public ProductsPage backToProducts() {
        click(BACK_TO_PRODUCTS, "Back to products");
        return new ProductsPage();
    }
}
