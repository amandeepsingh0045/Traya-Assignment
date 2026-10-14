package com.assignment.ui.tests;

import com.assignment.common.utils.JsonDataReader;
import com.assignment.ui.base.BaseTest;
import com.assignment.ui.models.CheckoutValidationCase;
import com.assignment.ui.models.Customer;
import com.assignment.ui.pages.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CheckoutTests extends BaseTest {

    private static final String CHECKOUT_DATA = "testdata/ui/checkout-data.json";
    private final JsonNode data = JsonDataReader.read(CHECKOUT_DATA);

    private Customer customer() {
        return JsonDataReader.read(CHECKOUT_DATA, CheckoutDataFile.class).customer;
    }

    /** Small holder so Jackson can map the "customer" node straight to the Lombok model. */
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class CheckoutDataFile { public Customer customer; }

    @Test(groups = {"smoke", "ui", "checkout", "e2e"},
          description = "Verify a customer can buy a product end-to-end and sees the order confirmation")
    public void customerCanPurchaseProductEndToEnd() {
        String product = data.get("product").asText();

        // Launch + login
        ProductsPage productsPage = loginAsDefaultUser();
        assertTrue(productsPage.isLoaded(), "Products page after login");

        // Select product + add to cart
        String listedPrice = productsPage.getProductPrice(product);
        ProductDetailsPage details = productsPage.selectProduct(product).addToCart();
        assertEquals(details.header().getCartItemCount(), 1, "Cart badge");

        // Verify cart
        CartPage cart = details.header().openCart();
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(cart.getProductNames(), List.of(product), "Cart products");
        soft.assertEquals(cart.getProductPrice(product), listedPrice, "Cart price");
        soft.assertEquals(cart.getProductQuantity(product), 1, "Cart quantity");
        soft.assertAll();

        // Checkout: shipping details
        CheckoutInformationPage information = cart.proceedToCheckout();
        assertTrue(information.isLoaded(), "Checkout information page");
        CheckoutOverviewPage overview = information.enterCustomerDetails(customer()).continueToOverview();

        // Verify order summary
        assertTrue(overview.isLoaded(), "Checkout overview page");
        assertEquals(overview.getProductNames(), List.of(product), "Products in order summary");
        assertEquals(overview.getItemTotal(), new BigDecimal(listedPrice.replace("$", "")), "Item total");
        assertEquals(overview.getTotal(), overview.getItemTotal().add(overview.getTax()), "Total = item total + tax");

        // Complete + confirmation
        CheckoutCompletePage complete = overview.finishOrder();
        assertTrue(complete.isLoaded(), "Order confirmation page");
        assertEquals(complete.getConfirmationHeader(), data.get("expectedConfirmationHeader").asText());
        assertEquals(complete.getConfirmationText(), data.get("expectedConfirmationText").asText());
        assertEquals(complete.header().getCartItemCount(), 0, "Cart should be empty after the order");
    }

    @DataProvider(name = "invalidCustomerDetails", parallel = true)
    public Object[][] invalidCustomerDetails() {
        return JsonDataReader.toDataProvider(
                JsonDataReader.readList(CHECKOUT_DATA, "invalidCustomerDetails", CheckoutValidationCase.class));
    }

    @Test(dataProvider = "invalidCustomerDetails", groups = {"regression", "ui", "checkout", "negative"},
          description = "Verify checkout is blocked when mandatory shipping details are missing")
    public void checkoutIsBlockedWhenShippingDetailsAreMissing(CheckoutValidationCase testCase) {
        String product = data.get("product").asText();

        CheckoutInformationPage information = loginAsDefaultUser()
                .selectProduct(product).addToCart()
                .header().openCart()
                .proceedToCheckout()
                .enterCustomerDetails(testCase.getCustomer())
                .continueExpectingError();

        assertEquals(information.getErrorMessage(), testCase.getExpectedError(), "Validation message");
        assertTrue(information.isLoaded(), "User should stay on the information step");
    }
}
