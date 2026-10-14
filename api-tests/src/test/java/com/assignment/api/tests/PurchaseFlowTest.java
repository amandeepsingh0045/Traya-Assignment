package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.models.store.Order;
import com.assignment.api.models.user.User;
import com.assignment.api.specs.ResponseSpecs;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Business flow showing response-to-request chaining across three resources:
 * user login (token) -> create pet (petId) -> place order for that petId -> verify -> cleanup.
 */
public class PurchaseFlowTest extends BaseApiTest {

    @Test(groups = {"smoke", "api", "chaining", "e2e"}, description = "Login -> create pet -> order pet -> verify")
    public void customerCanOrderNewlyListedPet() {
        // Login -> token
        User user = TestDataFactory.user();
        userClient.create(user).then().spec(ResponseSpecs.ok());
        String token = userClient.loginAndGetToken(user.getUsername(), user.getPassword());

        // Token -> create pet; response gives petId
        Pet pet = petClient.withToken(token).createAndGet(TestDataFactory.pet());

        // petId from pet response -> order request
        Order placed = storeClient.withToken(token)
                .placeOrder(TestDataFactory.orderFor(pet.getId()))
                .then().spec(ResponseSpecs.ok())
                .extract().as(Order.class);
        assertEquals(placed.getPetId(), pet.getId(), "Order should reference the created pet");
        assertEquals(placed.getStatus(), "placed");
        assertTrue(placed.getComplete());

        // orderId from order response -> GET order
        Order fetched = storeClient.getOrder(placed.getId()).then()
                .spec(ResponseSpecs.ok())
                .extract().as(Order.class);
        assertEquals(fetched.getId(), placed.getId());
        assertEquals(fetched.getPetId(), pet.getId());

        // Cleanup (also verifies DELETE)
        storeClient.deleteOrder(placed.getId()).then().spec(ResponseSpecs.ok());
        storeClient.getOrder(placed.getId()).then().spec(ResponseSpecs.notFound());
        petClient.delete(pet.getId()).then().spec(ResponseSpecs.ok());
    }
}
