package com.assignment.api.clients;

import com.assignment.api.specs.RequestBuilder;

/**
 * Base for all API clients. Holds the auth token so it can be passed fluently:
 * <pre>new PetClient().withToken(token).create(pet)</pre>
 * The self-type generic lets withToken() return the concrete client for method chaining.
 */
public abstract class BaseClient<T extends BaseClient<T>> {

    protected String token;

    @SuppressWarnings("unchecked")
    public T withToken(String token) {
        this.token = token;
        return (T) this;
    }

    /** Every request starts from the shared base spec + current token. */
    protected RequestBuilder request() {
        return RequestBuilder.newRequest().bearerToken(token);
    }
}
