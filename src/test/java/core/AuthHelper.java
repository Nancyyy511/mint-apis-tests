package core;

import io.restassured.specification.RequestSpecification;

/**
 * Public helper for authentication flows.
 * Keeps the test suite reusable and environment-driven.
 */
public final class AuthHelper {
    private AuthHelper() {}

    public static String loginWithBiometric(RequestSpecification baseSpec) {
        return C02_AuthHelper.loginWithBiometric(baseSpec);
    }

    public static String loginWithoutBiometric(RequestSpecification baseSpec) {
        return C02_AuthHelper.loginWithoutBiometric(baseSpec);
    }
}
