package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.NegativeCase;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.common.ApiResponse;
import com.assignment.api.specs.ResponseSpecs;
import com.assignment.common.utils.JsonDataReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

/**
 * Negative scenarios that Petstore actually enforces.
 * Spec rules Petstore does NOT enforce live in {@link PetSpecComplianceTest} (group "known-defect").
 */
public class PetNegativeTest extends BaseApiTest {

    private static final String PET_DATA = "testdata/api/pets.json";

    // ---------- invalid resource id ----------

    @DataProvider(name = "unknownIds")
    public Object[][] unknownIds() {
        return new Object[][]{{TestDataFactory.uniqueId()}, {-1L}, {0L}};
    }

    @Test(dataProvider = "unknownIds", groups = {"regression", "api", "negative"},
          description = "GET /pet/{id} with an unknown id returns 404 'Pet not found'")
    public void getPetWithUnknownId(long id) {
        ApiResponse error = petClient.getById(id).then()
                .spec(ResponseSpecs.notFound())
                .extract().as(ApiResponse.class);
        assertEquals(error.getMessage(), "Pet not found");
        assertEquals(error.getType(), "error");
    }

    @Test(groups = {"regression", "api", "negative"}, description = "GET /pet/{id} with non-numeric id returns 404")
    public void getPetWithNonNumericId() {
        petClient.getById("abc").then()
                .spec(ResponseSpecs.notFound())
                .body("message", containsString("NumberFormatException"));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "DELETE unknown pet returns 404")
    public void deleteUnknownPet() {
        petClient.delete(TestDataFactory.uniqueId()).then().spec(ResponseSpecs.notFound());
    }

    // ---------- invalid / missing input (data-driven from JSON) ----------

    @DataProvider(name = "rejectedPayloads")
    public Object[][] rejectedPayloads() {
        return JsonDataReader.toDataProvider(
                JsonDataReader.readList(PET_DATA, "rejectedPetPayloads", NegativeCase.class));
    }

    @Test(dataProvider = "rejectedPayloads", groups = {"regression", "api", "negative", "data-driven"},
          description = "Invalid request bodies are rejected with the correct status and message")
    public void createPetWithInvalidPayload(NegativeCase tc) {
        // textual payloads (malformed JSON) are sent as-is; object payloads are serialised
        String body = tc.getPayload().isTextual() ? tc.getPayload().asText() : tc.getPayload().toString();
        petClient.create(body).then()
                .spec(ResponseSpecs.expect().status(tc.getExpectedStatus()).build())
                .body("message", equalTo(tc.getExpectedMessage()));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "POST /pet with no body returns 405 'no data'")
    public void createPetWithEmptyBody() {
        petClient.create("").then()
                .spec(ResponseSpecs.expect().status(405).build())
                .body("message", equalTo("no data"));
    }
}
