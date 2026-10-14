package com.assignment.api.constants;

/** Single place for all Petstore resource paths. */
public final class Endpoints {

    private Endpoints() { }

    // Pet
    public static final String PET = "/pet";
    public static final String PET_BY_ID = "/pet/{petId}";
    public static final String PET_FIND_BY_STATUS = "/pet/findByStatus";

    // User
    public static final String USER = "/user";
    public static final String USER_BY_NAME = "/user/{username}";
    public static final String USER_LOGIN = "/user/login";
    public static final String USER_LOGOUT = "/user/logout";

    // Store
    public static final String STORE_ORDER = "/store/order";
    public static final String STORE_ORDER_BY_ID = "/store/order/{orderId}";
}
