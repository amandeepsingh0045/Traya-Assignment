package com.assignment.api.tests;

import com.assignment.api.base.BaseApiTest;
import com.assignment.api.data.PetCase;
import com.assignment.api.data.TestDataFactory;
import com.assignment.api.models.pet.Category;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.models.pet.Tag;
import com.assignment.api.specs.ResponseSpecs;
import com.assignment.common.utils.JsonDataReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/** Positive data-driven tests: every row in testdata/api/pets.json -> create + read back. */
public class PetDataDrivenTest extends BaseApiTest {

    @DataProvider(name = "validPets", parallel = true)
    public Object[][] validPets() {
        return JsonDataReader.toDataProvider(
                JsonDataReader.readList("testdata/api/pets.json", "validPets", PetCase.class));
    }

    @Test(dataProvider = "validPets", groups = {"regression", "api", "data-driven"},
          description = "Pets from JSON test data are created and can be read back unchanged")
    public void createAndReadPetFromTestData(PetCase tc) {
        Pet request = TestDataFactory.pet().toBuilder()
                .name(tc.getName())
                .status(tc.getStatus())
                .category(Category.builder().id(1L).name(tc.getCategory()).build())
                .tags(tc.getTags().stream().map(t -> Tag.builder().id(1L).name(t).build()).toList())
                .build();

        Pet created = petClient.create(request).then()
                .spec(ResponseSpecs.okWithSchema("schemas/pet-schema.json"))
                .extract().as(Pet.class);
        assertEquals(created, request, "Create response");

        Pet fetched = petClient.getById(request.getId()).then()
                .spec(ResponseSpecs.ok()).extract().as(Pet.class);
        assertEquals(fetched, request, "Read-back");

        petClient.delete(request.getId());   // best-effort cleanup
    }
}
