package com.assignment.api.base;

import com.assignment.api.clients.PetClient;
import com.assignment.api.clients.StoreClient;
import com.assignment.api.clients.UserClient;
import org.testng.annotations.BeforeMethod;

/** Fresh clients per test method -> no shared token/state between parallel tests. */
public abstract class BaseApiTest {

    protected PetClient petClient;
    protected UserClient userClient;
    protected StoreClient storeClient;

    @BeforeMethod(alwaysRun = true)
    public void initClients() {
        petClient = new PetClient();
        userClient = new UserClient();
        storeClient = new StoreClient();
    }
}
