package com.assignment.ui.tests;

import com.assignment.common.utils.JsonDataReader;
import com.assignment.ui.base.BaseTest;
import com.assignment.ui.models.LoginCredentials;
import com.assignment.ui.models.NegativeLoginCase;
import com.assignment.ui.pages.LoginPage;
import com.assignment.ui.pages.ProductsPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginTests extends BaseTest {

    private static final String LOGIN_DATA = "testdata/ui/login-data.json";

    @Test(groups = {"smoke", "ui", "login"},
          description = "Verify a standard user can log in and is taken to the Products page")
    public void standardUserCanLoginAndSeeProductsPage() {
        ProductsPage productsPage = loginAsDefaultUser();

        assertTrue(productsPage.isLoaded(), "Products page should load after a successful login");
        assertEquals(productsPage.getTitle(), "Products", "Page title after login");
        assertEquals(productsPage.header().getCartItemCount(), 0, "Cart should be empty for a fresh session");
    }

    @DataProvider(name = "acceptedUsers", parallel = true)
    public Object[][] acceptedUsers() {
        List<LoginCredentials> users = JsonDataReader.readList(LOGIN_DATA, "acceptedUsers", LoginCredentials.class);
        return JsonDataReader.toDataProvider(users);
    }

    @Test(dataProvider = "acceptedUsers", groups = {"regression", "ui", "login"},
          description = "Verify every accepted SauceDemo user can log in successfully")
    public void acceptedUserCanLogin(LoginCredentials user) {
        ProductsPage productsPage = openApplication().loginAs(user);

        assertTrue(productsPage.isLoaded(), "Products page should load for " + user);
    }

    @DataProvider(name = "invalidLogins", parallel = true)
    public Object[][] invalidLogins() {
        return JsonDataReader.toDataProvider(
                JsonDataReader.readList(LOGIN_DATA, "invalidLogins", NegativeLoginCase.class));
    }

    @Test(dataProvider = "invalidLogins", groups = {"regression", "ui", "login", "negative"},
          description = "Verify login is blocked and the correct error is shown")
    public void loginIsBlockedWithCorrectErrorMessage(NegativeLoginCase testCase) {
        LoginPage loginPage = openApplication().attemptLoginAs(testCase.credentials());

        assertEquals(loginPage.getErrorMessage(), testCase.getExpectedError(), "Error message");
        assertTrue(loginPage.isLoaded(), "User should remain on the login page");
    }
}
