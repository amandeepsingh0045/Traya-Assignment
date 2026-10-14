package com.assignment.ui.pages;

import com.assignment.ui.components.HeaderComponent;
import com.assignment.ui.utils.WaitUtils;
import org.openqa.selenium.By;

/** Inventory page shown after login (/inventory.html). */
public class ProductsPage extends BasePage {

    private static final By PAGE_TITLE = dataTest("title");
    private static final By INVENTORY_LIST = dataTest("inventory-list");

    private final HeaderComponent header = new HeaderComponent();

    public HeaderComponent header() {
        return header;
    }

    public boolean isLoaded() {
        return WaitUtils.urlContains(driver, "inventory.html") && isDisplayed(INVENTORY_LIST);
    }

    public String getTitle() {
        return getText(PAGE_TITLE);
    }

    /** Product name link located by its visible text (no list iteration -> no stale elements). */
    private static By productNameLink(String productName) {
        return By.xpath("//*[@data-test='inventory-item-name' and normalize-space()='" + productName + "']");
    }

    /** Price inside the same product card as the given name. */
    private static By productPrice(String productName) {
        return By.xpath("//*[@data-test='inventory-item-name' and normalize-space()='" + productName + "']"
                + "/ancestor::*[@data-test='inventory-item']//*[@data-test='inventory-item-price']");
    }

    public boolean isProductListed(String productName) {
        return isDisplayed(productNameLink(productName));
    }

    public String getProductPrice(String productName) {
        return getText(productPrice(productName));
    }

    /** Select (open) a product -> product details page. */
    public ProductDetailsPage selectProduct(String productName) {
        click(productNameLink(productName), "Product: " + productName);
        return new ProductDetailsPage();
    }
}
