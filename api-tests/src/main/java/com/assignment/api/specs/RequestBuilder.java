package com.assignment.api.specs;

import com.assignment.common.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Builder pattern over RestAssured's RequestSpecBuilder.
 * Every request starts from the same base spec (baseUri, JSON, logging filter) and
 * only adds what that particular call needs:
 *
 * <pre>
 * RequestSpecification spec = RequestBuilder.newRequest()
 *         .bearerToken(token)
 *         .pathParam("petId", 10)
 *         .build();
 * </pre>
 */
public final class RequestBuilder {

    private final RequestSpecBuilder builder;

    private RequestBuilder() {
        this.builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("api.baseUrl"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new LoggingFilter());
    }

    /** Entry point - always returns a fresh builder (thread-safe for parallel runs). */
    public static RequestBuilder newRequest() {
        return new RequestBuilder();
    }

    /** Adds "Authorization: Bearer &lt;token&gt;" when a token is present (token from login API). */
    public RequestBuilder bearerToken(String token) {
        if (token != null && !token.isBlank()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        return this;
    }

    /** Petstore's api_key header (required by DELETE /pet). */
    public RequestBuilder apiKey() {
        return apiKey(ConfigManager.get("api.key"));
    }

    public RequestBuilder apiKey(String key) {
        builder.addHeader("api_key", key);
        return this;
    }

    public RequestBuilder header(String name, String value) {
        builder.addHeader(name, value);
        return this;
    }

    public RequestBuilder pathParam(String name, Object value) {
        builder.addPathParam(name, value);
        return this;
    }

    public RequestBuilder queryParam(String name, Object value) {
        builder.addQueryParam(name, value);
        return this;
    }

    /** POJO body (serialised by Jackson) or raw String body. */
    public RequestBuilder body(Object body) {
        builder.setBody(body);
        return this;
    }

    public RequestSpecification build() {
        return builder.build();
    }
}
