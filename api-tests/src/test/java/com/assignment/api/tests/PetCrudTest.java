package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.common.ApiResponse;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.specs.ResponseSpecs;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class PetCrudTest extends BaseApiTest {

    private static final String PET_SCHEMA = "schemas/pet-schema.json";

    @Test(groups = {"smoke", "api"}, description = "POST /pet creates a pet")
    public void createPet() {
        Pet request = TestDataFactory.pet();

        Pet response = petClient.create(request).then()
                .spec(ResponseSpecs.okWithSchema(PET_SCHEMA))
                .extract().as(Pet.class);

        // Lombok @Data gives equals() -> whole request/response comparison in one assertion
        assertEquals(response, request, "Created pet should match request payload");
    }

    @Test(groups = {"smoke", "api"}, description = "GET /pet/{id} returns the pet created by POST (chained)")
    public void getPetById() {
        Pet created = petClient.createAndGet(TestDataFactory.pet());

        Pet fetched = petClient.getById(created.getId()).then()
                .spec(ResponseSpecs.okWithSchema(PET_SCHEMA))
                .extract().as(Pet.class);

        assertEquals(fetched, created);
    }

    @Test(groups = {"regression", "api"}, description = "PUT /pet updates name and status")
    public void updatePet() {
        Pet created = petClient.createAndGet(TestDataFactory.pet());
        Pet updateRequest = created.toBuilder().name("updated-" + created.getId()).status("sold").build();

        petClient.update(updateRequest).then()
                .spec(ResponseSpecs.expect().status(200).json()
                        .body("name", equalTo(updateRequest.getName()))
                        .body("status", equalTo("sold"))
                        .build());

        Pet fetched = petClient.getById(created.getId()).then().spec(ResponseSpecs.ok()).extract().as(Pet.class);
        assertEquals(fetched.getStatus(), "sold");
    }

    @Test(groups = {"regression", "api"}, description = "DELETE /pet/{id} then GET returns 404")
    public void deletePet() {
        Pet created = petClient.createAndGet(TestDataFactory.pet());

        ApiResponse deleted = petClient.delete(created.getId()).then()
                .spec(ResponseSpecs.ok())
                .extract().as(ApiResponse.class);
        assertEquals(deleted.getMessage(), String.valueOf(created.getId()));

        petClient.getById(created.getId()).then()
                .spec(ResponseSpecs.notFound())
                .body("message", equalTo("Pet not found"));
    }

    @DataProvider(name = "statuses", parallel = true)
    public Object[][] statuses() {
        return new Object[][]{{"available"}, {"pending"}, {"sold"}};
    }

    @Test(dataProvider = "statuses", groups = {"regression", "api"},
          description = "GET /pet/findByStatus returns only pets of that status, incl. one we just created")
    public void findByStatus(String status) {
        // Seed our own pet so the test doesn't depend on shared public data
        Pet seeded = petClient.createAndGet(TestDataFactory.pet().toBuilder().status(status).build());

        petClient.findByStatus(status).then()
                .spec(ResponseSpecs.ok())
                .body("status", everyItem(equalTo(status)))
                .body("id", hasItem(seeded.getId()));

        petClient.delete(seeded.getId());
    }
}
