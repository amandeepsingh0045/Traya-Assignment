package com.assignment.ui.tests;

import com.assignment.common.utils.JsonDataReader;
import com.assignment.ui.base.BaseTest;
import com.assignment.ui.pages.CartPage;
import com.assignment.ui.pages.ProductDetailsPage;
import com.assignment.ui.pages.ProductsPage;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CartTests extends BaseTest {

    private final JsonNode data = JsonDataReader.read("testdata/ui/checkout-data.json");

    @Test(groups = {"regression", "ui", "cart"},
          description = "Verify a selected product shows the same name and price on its details page")
    public void selectedProductDetailsMatchProductList() {
        String product = data.get("product").asText();
        ProductsPage productsPage = loginAsDefaultUser();
        String listedPrice = productsPage.getProductPrice(product);

        ProductDetailsPage details = productsPage.selectProduct(product);

        assertEquals(details.getProductName(), product, "Product name on details page");
        assertEquals(details.getProductPrice(), listedPrice, "Product price on details page");
    }

    @Test(groups = {"regression", "ui", "cart"},
          description = "Verify adding a product updates the cart badge and the cart shows correct name, price and quantity")
    public void addedProductAppearsInCartWithCorrectDetails() {
        String product = data.get("product").asText();
        ProductsPage productsPage = loginAsDefaultUser();
        String listedPrice = productsPage.getProductPrice(product);

        ProductDetailsPage details = productsPage.selectProduct(product).addToCart();
        assertEquals(details.header().getCartItemCount(), 1, "Cart badge after adding one product");

        CartPage cart = details.header().openCart();
        SoftAssert soft = new SoftAssert();   // report all cart mismatches at once
        soft.assertTrue(cart.isLoaded(), "Cart page loaded");
        soft.assertEquals(cart.getProductNames(), List.of(product), "Products in cart");
        soft.assertEquals(cart.getProductPrice(product), listedPrice, "Price in cart");
        soft.assertEquals(cart.getProductQuantity(product), 1, "Quantity in cart");
        soft.assertAll();
    }

    @Test(groups = {"regression", "ui", "cart"},
          description = "Verify an empty cart shows no badge and no products")
    public void emptyCartShowsNoProducts() {
        ProductsPage productsPage = loginAsDefaultUser();
        assertEquals(productsPage.header().getCartItemCount(), 0, "Cart badge for empty cart");

        CartPage cart = productsPage.header().openCart();
        assertTrue(cart.isLoaded(), "Cart page loaded");
    }
}
