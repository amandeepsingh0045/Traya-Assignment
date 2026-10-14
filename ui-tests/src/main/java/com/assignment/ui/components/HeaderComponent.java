package com.assignment.ui.components;

import com.assignment.ui.pages.BasePage;
import com.assignment.ui.pages.CartPage;
import org.openqa.selenium.By;

/**
 * Reusable COMPONENT (not a page): the header with the cart icon appears on every logged-in page,
 * so it is modelled once and composed into those pages via header().
 */
public class HeaderComponent extends BasePage {

    private static final By CART_LINK = dataTest("shopping-cart-link");
    private static final By CART_BADGE = dataTest("shopping-cart-badge");

    /** Number shown on the cart badge; 0 when the badge is absent (empty cart). */
    public int getCartItemCount() {
        return driver.findElements(CART_BADGE).isEmpty() ? 0 : Integer.parseInt(getText(CART_BADGE));
    }

    public CartPage openCart() {
        click(CART_LINK, "Cart icon");
        return new CartPage();
    }
}
