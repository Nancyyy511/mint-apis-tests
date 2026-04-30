package core;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public  class C01_BaseTest {

    protected static RequestSpecification baseSpec;
    protected static RequestSpecification authSpec;
    protected static String userToken;


    @BeforeAll
    static void setup() {
        baseSpec = RequestSpecFactory.baseSpec();

        // Login once per test class (choose a single explicit flow for suite-wide authenticated tests)
        userToken = AuthHelper.loginWithBiometric(baseSpec);

        // Auth spec (base + bearer token)
        authSpec = RequestSpecFactory.authSpec(baseSpec, userToken);
    }
}
