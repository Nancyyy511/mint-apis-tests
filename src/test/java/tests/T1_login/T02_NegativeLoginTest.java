package tests.T1_login;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import services.S1_login.S01_AuthService;
import core.C00_BaseSpecTest;
import utils.Config;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class T02_NegativeLoginTest extends C00_BaseSpecTest {

    @Test
    void login_should_fail_with_wrong_password() {

        String username = Config.username();
        Config.requireNonBlank(username, "MINT_USERNAME", "Set env var MINT_USERNAME (recommended) or config.properties mint.username for local.");

        String body = """
        {
          "email": "%s",
          "password": "WRONG_PASS",
          "useBiometric": 1
        }
        """.formatted(username.replace("\\", "\\\\").replace("\"", "\\\""));

        Response response = S01_AuthService.login(baseSpec , body);

        response.then()
                .statusCode(anyOf(is(400), is(401)))
                .body("status", equalTo(false))
                .body("message", notNullValue())
                .body("data", nullValue());
    }
    @Test
    void login_should_fail_with_wrong_email() {

        String password = Config.password();
        Config.requireNonBlank(password, "MINT_PASSWORD", "Set env var MINT_PASSWORD (recommended) or config.properties mint.password for local.");

        String body = """
    {
      "email": "0000000000",
      "password": "%s",
      "useBiometric": 1
    }
    """.formatted(password.replace("\\", "\\\\").replace("\"", "\\\""));

        Response response = S01_AuthService.login(baseSpec,body);

        response.then()
                .statusCode(anyOf(is(400), is(401)))
                .body("status", equalTo(false))
                .body("message", notNullValue());
    }


    @Test
    void login_should_fail_when_password_is_missing() {

        String username = Config.username();
        Config.requireNonBlank(username, "MINT_USERNAME", "Set env var MINT_USERNAME (recommended) or config.properties mint.username for local.");

        String body = """
    {
      "email": "%s",
      "useBiometric": 1
    }
    """.formatted(username.replace("\\", "\\\\").replace("\"", "\\\""));

        Response response = S01_AuthService.login(baseSpec , body);

        response.then()
                .statusCode(400)
                .body("status", equalTo(false))
                .body("message", notNullValue());
    }

    @Test
    void login_should_fail_when_email_is_null() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": null,
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_when_email_is_empty() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": "",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_when_password_is_null() {
        // Arrange
        String username = requireUsername();
        String body = """
                {
                  "email": "%s",
                  "password": null,
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_when_password_is_empty() {
        // Arrange
        String username = requireUsername();
        String body = """
                {
                  "email": "%s",
                  "password": "",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_when_fields_missing() {
        // Arrange
        String body = """
                {
                }
                """;

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_very_long_username() {
        // Arrange
        String password = requirePassword();
        String longUsername = "1".repeat(512);
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(longUsername), escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_special_characters() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": "!@#$%^&*()_+=-{}[]|\\\\:;\\\"'<>,.?/",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_unicode_input() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson("مستخدم١٢٣"), escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_sql_injection() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson("' OR 1=1 --"), escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_xss_payload() {
        // Arrange
        String password = requirePassword();
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson("<script>alert(1)</script>"), escapeJson(password));

        // Act
        Response response = S01_AuthService.login(baseSpec, body);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_without_imei() {
        // Arrange
        String username = requireUsername();
        String password = requirePassword();
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username), escapeJson(password));

        // Act
        Response response = given()
                .spec(baseSpec)
                .header("imei", "")
                .body(body)
                .when()
                .post("/api/v1/auth/user/login");

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_with_invalid_min_ios_version() {
        // Arrange
        String username = requireUsername();
        String password = requirePassword();
        String body = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username), escapeJson(password));

        // Act
        Response response = given()
                .spec(baseSpec)
                .header("min-ios-version", "invalid")
                .body(body)
                .when()
                .post("/api/v1/auth/user/login");

        // Assert
        assertFailure(response);
    }

    @Test
    void login_without_biometric_should_fail_without_2fa_answer() {
        // Arrange
        String username = requireUsername();
        String password = requirePassword();

        String loginBody = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 0
                }
                """.formatted(escapeJson(username), escapeJson(password));

        // Act: Login succeeds but flow should fail when validating 2FA without an answer
        Response loginResponse = S01_AuthService.login(baseSpec, loginBody);
        loginResponse.then().statusCode(201).body("status", equalTo(true));

        String guestToken = loginResponse.jsonPath().getString("data.guestToken");
        assertNotNull(guestToken, "guestToken should not be null");

        Response questionsResponse = S01_AuthService.get2faQuestions(baseSpec, guestToken);
        questionsResponse.then().statusCode(200).body("status", equalTo(true));

        Integer questionId = questionsResponse.jsonPath().getInt("data[0].QuestionId");
        assertNotNull(questionId, "2FA questionId should not be null");

        String validateBodyMissingAnswer = """
                {
                  "questionId": %d
                }
                """.formatted(questionId);

        Response validateResponse = S01_AuthService.validate2faQuestion(baseSpec, validateBodyMissingAnswer, guestToken);

        // Assert
        assertFailure(validateResponse);
    }

    @Test
    void verify_pin_should_fail_with_wrong_pin() {
        // Arrange
        String username = requireUsername();
        String password = requirePassword();

        String loginBody = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username), escapeJson(password));

        Response loginResponse = S01_AuthService.login(baseSpec, loginBody);
        loginResponse.then().statusCode(201).body("status", equalTo(true));

        String guestToken = loginResponse.jsonPath().getString("data.guestToken");
        assertNotNull(guestToken, "guestToken should not be null");

        // Act
        Response response = S01_AuthService.verifyPin(baseSpec, """
                { "pin": "9999" }
                """, guestToken);

        // Assert
        assertFailure(response);
    }

    @Test
    void verify_pin_should_fail_with_empty_pin() {
        // Arrange
        String username = requireUsername();
        String password = requirePassword();

        String loginBody = """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username), escapeJson(password));

        Response loginResponse = S01_AuthService.login(baseSpec, loginBody);
        loginResponse.then().statusCode(201).body("status", equalTo(true));

        String guestToken = loginResponse.jsonPath().getString("data.guestToken");
        assertNotNull(guestToken, "guestToken should not be null");

        // Act
        Response response = S01_AuthService.verifyPin(baseSpec, """
                { "pin": "" }
                """, guestToken);

        // Assert
        assertFailure(response);
    }

    @Test
    void login_should_fail_after_multiple_attempts() {
        // Arrange
        String username = requireUsername();

        String body = """
                {
                  "email": "%s",
                  "password": "WRONG_PASS",
                  "useBiometric": 1
                }
                """.formatted(escapeJson(username));

        // Act
        Response last = null;
        for (int i = 0; i < 10; i++) {
            last = S01_AuthService.login(baseSpec, body);
        }

        // Assert
        assertNotNull(last, "Last response should not be null");
        assertFailure(last);
    }

    private static void assertFailure(Response response) {
        response.then().log().all();
        response.then()
                .statusCode(anyOf(is(400), is(401), is(403), is(422)))
                .body("status", equalTo(false));

        assertMessageOrErrorsExists(response);
    }

    private static void assertMessageOrErrorsExists(Response response) {
        String message = null;
        try {
            message = response.jsonPath().getString("message");
        } catch (Exception ignored) {
        }

        List<Object> errors = null;
        try {
            errors = response.jsonPath().getList("errors");
        } catch (Exception ignored) {
        }

        boolean hasMessage = message != null && !message.trim().isEmpty();
        boolean hasErrors = errors != null && !errors.isEmpty();

        assertTrue(hasMessage || hasErrors, "Expected 'message' or 'errors' to exist in error response");
    }

    private static String requireUsername() {
        String username = Config.username();
        Config.requireNonBlank(username, "MINT_USERNAME", "Set env var MINT_USERNAME (recommended) or config.properties mint.username for local.");
        return username;
    }

    private static String requirePassword() {
        String password = Config.password();
        Config.requireNonBlank(password, "MINT_PASSWORD", "Set env var MINT_PASSWORD (recommended) or config.properties mint.password for local.");
        return password;
    }

    private static String escapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
