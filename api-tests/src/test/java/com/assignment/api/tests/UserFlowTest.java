package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.common.ApiResponse;
import com.assignment.api.models.user.User;
import com.assignment.api.specs.ResponseSpecs;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/** User lifecycle with chaining: create -> login (token) -> get/update with token -> logout -> delete. */
public class UserFlowTest extends BaseApiTest {

    @Test(groups = {"smoke", "api", "chaining"}, description = "Token from login is reused in follow-up calls")
    public void userLifecycleWithLoginToken() {
        // 1. Create user
        User user = TestDataFactory.user();
        ApiResponse created = userClient.create(user).then()
                .spec(ResponseSpecs.ok())
                .extract().as(ApiResponse.class);
        assertEquals(created.getMessage(), String.valueOf(user.getId()), "Create returns the user id");

        // 2. Login -> extract token
        String token = userClient.loginAndGetToken(user.getUsername(), user.getPassword());
        assertFalse(token.isBlank(), "Session token should be returned");

        // 3. Use token for subsequent calls (method chaining via withToken)
        User fetched = userClient.withToken(token).get(user.getUsername()).then()
                .spec(ResponseSpecs.ok())
                .extract().as(User.class);
        assertEquals(fetched, user, "Fetched user should match created user");

        // 4. Update using a copy of the fetched response
        User updated = fetched.toBuilder().firstName("Jane").build();
        userClient.update(user.getUsername(), updated).then().spec(ResponseSpecs.ok());
        userClient.get(user.getUsername()).then()
                .spec(ResponseSpecs.ok())
                .body("firstName", equalTo("Jane"));

        // 5. Logout + cleanup
        userClient.logout().then().spec(ResponseSpecs.ok()).body("message", equalTo("ok"));
        userClient.delete(user.getUsername()).then().spec(ResponseSpecs.ok());
        userClient.get(user.getUsername()).then().spec(ResponseSpecs.notFound());
    }
}
