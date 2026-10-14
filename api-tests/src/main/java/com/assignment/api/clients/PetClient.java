package com.assignment.api.clients;

import com.assignment.api.constants.Endpoints;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.specs.ResponseSpecs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetClient extends BaseClient<PetClient> {

    public Response create(Object pet) {
        return given(request().body(pet).build()).post(Endpoints.PET);
    }

    public Response update(Object pet) {
        return given(request().body(pet).build()).put(Endpoints.PET);
    }

    public Response getById(Object petId) {
        return given(request().pathParam("petId", petId).build()).get(Endpoints.PET_BY_ID);
    }

    public Response findByStatus(String status) {
        return given(request().queryParam("status", status).build()).get(Endpoints.PET_FIND_BY_STATUS);
    }

    public Response delete(Object petId) {
        return given(request().apiKey().pathParam("petId", petId).build()).delete(Endpoints.PET_BY_ID);
    }

    /** Delete with an explicit api_key (null = header omitted) - for auth negative tests. */
    public Response deleteWithApiKey(Object petId, String apiKey) {
        var req = request().pathParam("petId", petId);
        if (apiKey != null) req.apiKey(apiKey);
        return given(req.build()).delete(Endpoints.PET_BY_ID);
    }

    /** Convenience for chaining: create, validate 200, return the deserialised Pet. */
    public Pet createAndGet(Pet pet) {
        return create(pet).then().spec(ResponseSpecs.ok()).extract().as(Pet.class);
    }
}
