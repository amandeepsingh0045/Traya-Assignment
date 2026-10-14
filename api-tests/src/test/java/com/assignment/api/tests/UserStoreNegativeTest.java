package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.specs.ResponseSpecs;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/** Negative scenarios for /user and /store that Petstore enforces. */
public class UserStoreNegativeTest extends BaseApiTest {

    @Test(groups = {"regression", "api", "negative"}, description = "GET unknown user returns 404 'User not found'")
    public void getUnknownUser() {
        userClient.get("ghost_" + TestDataFactory.uniqueId()).then()
                .spec(ResponseSpecs.notFound())
                .body("message", equalTo("User not found"));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "DELETE unknown user returns 404")
    public void deleteUnknownUser() {
        userClient.delete("ghost_" + TestDataFactory.uniqueId()).then().spec(ResponseSpecs.notFound());
    }

    @Test(groups = {"regression", "api", "negative"}, description = "GET unknown order returns 404 'Order not found'")
    public void getUnknownOrder() {
        storeClient.getOrder(TestDataFactory.uniqueId()).then()
                .spec(ResponseSpecs.notFound())
                .body("message", equalTo("Order not found"));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "GET order with non-numeric id returns 404")
    public void getOrderWithNonNumericId() {
        storeClient.getOrder("abc").then()
                .spec(ResponseSpecs.notFound())
                .body("message", containsString("NumberFormatException"));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "DELETE unknown order returns 404")
    public void deleteUnknownOrder() {
        storeClient.deleteOrder(TestDataFactory.uniqueId()).then().spec(ResponseSpecs.notFound());
    }
}
