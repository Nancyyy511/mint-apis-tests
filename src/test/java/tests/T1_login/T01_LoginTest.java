package tests.T1_login;

import core.AuthHelper;
import core.C00_BaseSpecTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class T01_LoginTest extends C00_BaseSpecTest {

    @Test
    void login_with_biometric_should_succeed() {
        // Arrange: baseSpec is initialized in C00_BaseSpecTest

        // Act
        String token = AuthHelper.loginWithBiometric(baseSpec);

        // Assert
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Test
    void login_without_biometric_should_succeed() {
        // Arrange: baseSpec is initialized in C00_BaseSpecTest

        // Act
        String token = AuthHelper.loginWithoutBiometric(baseSpec);

        // Assert
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }
}
