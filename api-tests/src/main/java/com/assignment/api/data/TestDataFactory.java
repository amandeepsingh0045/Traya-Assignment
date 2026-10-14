package com.assignment.api.data;

import com.assignment.api.models.pet.Category;
import com.assignment.api.models.pet.Pet;
import com.assignment.api.models.pet.Tag;
import com.assignment.api.models.store.Order;
import com.assignment.api.models.user.User;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates valid default payloads via Lombok builders. Tests tweak only what they care about:
 * <pre>TestDataFactory.pet().toBuilder().status("sold").build()</pre>
 * Unique IDs keep tests independent on the shared public Petstore.
 */
public final class TestDataFactory {

    private TestDataFactory() { }

    public static long uniqueId() {
        return ThreadLocalRandom.current().nextLong(9_000_000_000L, 9_999_999_999L);
    }

    public static Pet pet() {
        long id = uniqueId();
        return Pet.builder()
                .id(id)
                .name("doggie-" + id)
                .category(Category.builder().id(1L).name("Dogs").build())
                .photoUrls(List.of("https://example.com/dog.png"))
                .tags(List.of(Tag.builder().id(1L).name("automation").build()))
                .status("available")
                .build();
    }

    public static User user() {
        long id = uniqueId();
        return User.builder()
                .id(id)
                .username("user_" + id)
                .firstName("John")
                .lastName("Doe")
                .email("user_" + id + "@test.com")
                .password("Pass@" + id)
                .phone("9999999999")
                .userStatus(1)
                .build();
    }

    public static Order orderFor(long petId) {
        return Order.builder()
                .id(uniqueId())
                .petId(petId)
                .quantity(1)
                .shipDate(OffsetDateTime.now(ZoneOffset.UTC).toString())
                .status("placed")
                .complete(true)
                .build();
    }
}
