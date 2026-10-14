package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.NegativeCase;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.specs.ResponseSpecs;
import com.assignment.common.utils.JsonDataReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Assertions written against the OpenAPI contract (https://petstore.swagger.io/v2/swagger.json),
 * NOT against current behaviour. The public Petstore does not enforce these rules, so these tests
 * FAIL today - they are real findings, tagged "known-defect" and excluded from the regression suite.
 *
 * Run them explicitly:  ./gradlew :api-tests:test -Psuite=known-defects
 */
public class PetSpecComplianceTest extends BaseApiTest {

    @DataProvider(name = "specViolations")
    public Object[][] specViolations() {
        return JsonDataReader.toDataProvider(
                JsonDataReader.readList("testdata/api/pets.json", "specViolations", NegativeCase.class));
    }

    @Test(dataProvider = "specViolations", groups = {"known-defect", "api", "negative"},
          description = "Spec: missing required fields / invalid enum should be rejected with 405 Invalid input")
    public void invalidPetShouldBeRejected(NegativeCase tc) {
        petClient.create(tc.getPayload().toString()).then()
                .spec(ResponseSpecs.expect().status(tc.getExpectedStatus()).build());
    }

    @Test(groups = {"known-defect", "api", "negative", "auth"},
          description = "Spec: DELETE /pet without api_key should be rejected")
    public void deletePetWithoutApiKeyShouldBeRejected() {
        Pet pet = petClient.createAndGet(TestDataFactory.pet());
        petClient.deleteWithApiKey(pet.getId(), null).then()
                .spec(ResponseSpecs.expect().status(401).build());
    }

    @Test(groups = {"known-defect", "api", "negative", "auth"},
          description = "Spec: DELETE /pet with a wrong api_key should be rejected")
    public void deletePetWithWrongApiKeyShouldBeRejected() {
        Pet pet = petClient.createAndGet(TestDataFactory.pet());
        petClient.deleteWithApiKey(pet.getId(), "wrong-key").then()
                .spec(ResponseSpecs.expect().status(401).build());
    }

    @Test(groups = {"known-defect", "api", "negative", "auth"},
          description = "Spec: login with invalid credentials should return 400")
    public void loginWithInvalidCredentialsShouldFail() {
        userClient.login("user_does_not_exist_" + TestDataFactory.uniqueId(), "wrong-password").then()
                .spec(ResponseSpecs.expect().status(400).build())
                .body("message", not(startsWith("logged in user session")));
    }
}
