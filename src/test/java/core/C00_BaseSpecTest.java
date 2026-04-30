package core;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class that only builds a configured base RequestSpecification.
 * Does not perform login and does not require credentials.
 */
public class C00_BaseSpecTest {
    protected static RequestSpecification baseSpec;

    @BeforeAll
    static void setupBaseSpec() {
        baseSpec = RequestSpecFactory.baseSpec();
    }
}

