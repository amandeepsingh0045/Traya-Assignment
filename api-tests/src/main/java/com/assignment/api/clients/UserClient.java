package com.assignment.api.clients;

import com.assignment.api.constants.Endpoints;
import com.assignment.api.models.common.ApiResponse;
import com.assignment.api.models.user.User;
import com.assignment.api.specs.ResponseSpecs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient<UserClient> {

    private static final String SESSION_PREFIX = "logged in user session:";

    public Response create(User user) {
        return given(request().body(user).build()).post(Endpoints.USER);
    }

    public Response get(String username) {
        return given(request().pathParam("username", username).build()).get(Endpoints.USER_BY_NAME);
    }

    public Response update(String username, User user) {
        return given(request().pathParam("username", username).body(user).build()).put(Endpoints.USER_BY_NAME);
    }

    public Response delete(String username) {
        return given(request().pathParam("username", username).build()).delete(Endpoints.USER_BY_NAME);
    }

    public Response login(String username, String password) {
        return given(request().queryParam("username", username).queryParam("password", password).build())
                .get(Endpoints.USER_LOGIN);
    }

    public Response logout() {
        return given(request().build()).get(Endpoints.USER_LOGOUT);
    }

    /**
     * Logs in and extracts the session token from the response message
     * ("logged in user session:1697..."), so it can be chained into other clients.
     */
    public String loginAndGetToken(String username, String password) {
        ApiResponse body = login(username, password).then()
                .spec(ResponseSpecs.ok())
                .extract().as(ApiResponse.class);
        String message = body.getMessage();
        if (message == null || !message.startsWith(SESSION_PREFIX)) {
            throw new IllegalStateException("Unexpected login response: " + message);
        }
        return message.substring(SESSION_PREFIX.length());
    }
}
