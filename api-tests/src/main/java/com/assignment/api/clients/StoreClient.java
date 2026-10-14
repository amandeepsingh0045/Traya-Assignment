package com.assignment.api.clients;

import com.assignment.api.constants.Endpoints;
import com.assignment.api.models.store.Order;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class StoreClient extends BaseClient<StoreClient> {

    public Response placeOrder(Order order) {
        return given(request().body(order).build()).post(Endpoints.STORE_ORDER);
    }

    public Response getOrder(Object orderId) {
        return given(request().pathParam("orderId", orderId).build()).get(Endpoints.STORE_ORDER_BY_ID);
    }

    public Response deleteOrder(Object orderId) {
        return given(request().pathParam("orderId", orderId).build()).delete(Endpoints.STORE_ORDER_BY_ID);
    }
}
