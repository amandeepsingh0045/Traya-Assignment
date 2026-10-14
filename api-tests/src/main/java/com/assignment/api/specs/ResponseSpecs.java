package com.assignment.api.specs;

import com.assignment.common.config.ConfigManager;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;

/**
 * Builder pattern for reusable response expectations.
 *
 * <pre>
 * response.then().spec(ResponseSpecs.expect().status(200).json().withinSla()
 *                                     .schema("schemas/pet-schema.json").build());
 * </pre>
 * Common combinations are exposed as presets: {@link #ok()}, {@link #okWithSchema(String)}, {@link #notFound()}.
 */
public final class ResponseSpecs {

    private final ResponseSpecBuilder builder = new ResponseSpecBuilder();

    private ResponseSpecs() { }

    public static ResponseSpecs expect() {
        return new ResponseSpecs();
    }

    public ResponseSpecs status(int statusCode) {
        builder.expectStatusCode(statusCode);
        return this;
    }

    public ResponseSpecs json() {
        builder.expectContentType(ContentType.JSON);
        return this;
    }

    public ResponseSpecs within(long millis) {
        builder.expectResponseTime(lessThan(millis), TimeUnit.MILLISECONDS);
        return this;
    }

    /** Response-time SLA from config (api.maxResponseMs). */
    public ResponseSpecs withinSla() {
        return within(ConfigManager.getInt("api.maxResponseMs", 5000));
    }

    public ResponseSpecs schema(String classpathSchema) {
        builder.expectBody(matchesJsonSchemaInClasspath(classpathSchema));
        return this;
    }

    public ResponseSpecs body(String jsonPath, Matcher<?> matcher) {
        builder.expectBody(jsonPath, matcher);
        return this;
    }

    public ResponseSpecification build() {
        return builder.build();
    }

    // ---------- presets ----------

    public static ResponseSpecification ok() {
        return expect().status(200).json().withinSla().build();
    }

    public static ResponseSpecification okWithSchema(String classpathSchema) {
        return expect().status(200).json().withinSla().schema(classpathSchema).build();
    }

    public static ResponseSpecification notFound() {
        return expect().status(404).build();
    }
}
