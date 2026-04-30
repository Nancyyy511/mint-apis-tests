package core;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import services.S1_login.S01_AuthService;
import utils.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class C02_AuthHelper {

    /**
     * Flow 1 (Biometric):
     * Login (useBiometric=1) -> Verify PIN
     */
    public static String loginWithBiometric(RequestSpecification baseSpec) {
        String username = requireUsername();
        String password = requirePassword();
        String pin = requirePin();

        Response loginResponse = S01_AuthService.login(
                baseSpec,
                buildLoginBody(username, password, 1)
        );

        // Strict Postman status code: 201
        loginResponse.then().log().all();
        loginResponse.then()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("data.guestToken", notNullValue())
                .body("data.isUserActive", equalTo(true));

        String guestToken = loginResponse.jsonPath().getString("data.guestToken");
        assertNotNull(guestToken, "guestToken should not be null");
        assertFalse(guestToken.isEmpty(), "guestToken should not be empty");

        Response verifyPinResponse = S01_AuthService.verifyPin(
                baseSpec,
                buildPinBody(pin),
                guestToken
        );

        // Strict Postman status code: 201
        verifyPinResponse.then().log().all();
        verifyPinResponse.then()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("data.token", notNullValue())
                .body("data.user.isUserActive", equalTo(true));

        String token = verifyPinResponse.jsonPath().getString("data.token");
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");

        return token;
    }

    /**
     * Flow 2 (Non-Biometric):
     * Login (useBiometric=0) -> Get 2FA Questions -> Validate 2FA Answer -> Verify PIN
     */
    public static String loginWithoutBiometric(RequestSpecification baseSpec) {
        String username = requireUsername();
        String password = requirePassword();
        String pin = requirePin();
        String answer = requireSecurityAnswer();

        Response loginResponse = S01_AuthService.login(
                baseSpec,
                buildLoginBody(username, password, 0)
        );

        // Strict Postman status code: 201
        loginResponse.then().log().all();
        loginResponse.then()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("data.guestToken", notNullValue())
                .body("data.isUserActive", equalTo(true));

        String guestToken = loginResponse.jsonPath().getString("data.guestToken");
        assertNotNull(guestToken, "guestToken should not be null");
        assertFalse(guestToken.isEmpty(), "guestToken should not be empty");

        Response questionsResponse = S01_AuthService.get2faQuestions(baseSpec, guestToken);

        // Strict Postman status code: 200
        questionsResponse.then().log().all();
        questionsResponse.then()
                .statusCode(200)
                .body("status", equalTo(true));

        Integer questionId = questionsResponse.jsonPath().getInt("data[0].QuestionId");
        assertNotNull(questionId, "2FA questionId should not be null");

        Response validateResponse = S01_AuthService.validate2faQuestion(
                baseSpec,
                build2faBody(questionId, answer),
                guestToken
        );

        // Strict Postman status code: 200
        validateResponse.then().log().all();
        validateResponse.then()
                .statusCode(200)
                .body("status", equalTo(true));

        Response verifyPinResponse = S01_AuthService.verifyPin(
                baseSpec,
                buildPinBody(pin),
                guestToken
        );

        // Strict Postman status code: 201
        verifyPinResponse.then().log().all();
        verifyPinResponse.then()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("data.token", notNullValue())
                .body("data.user.isUserActive", equalTo(true));

        String token = verifyPinResponse.jsonPath().getString("data.token");
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");

        return token;
    }

    private static String requireUsername() {
        String username = Config.username(); // field name stays "email"; value is numeric username
        Config.requireNonBlank(
                username,
                "MINT_USERNAME",
                "Set env var MINT_USERNAME (recommended) or config.properties mint.username for local."
        );
        return username;
    }

    private static String requirePassword() {
        String password = Config.password();
        Config.requireNonBlank(
                password,
                "MINT_PASSWORD",
                "Set env var MINT_PASSWORD (recommended) or config.properties mint.password for local."
        );
        return password;
    }

    private static String requirePin() {
        String pin = Config.pin();
        Config.requireNonBlank(
                pin,
                "MINT_PIN",
                "Set env var MINT_PIN (recommended) or config.properties mint.pin for local."
        );
        return pin;
    }

    private static String requireSecurityAnswer() {
        String answer = Config.securityAnswer();
        Config.requireNonBlank(
                answer,
                "MINT_SECURITY_ANSWER",
                "Non-biometric flow requires 2FA. Set env var MINT_SECURITY_ANSWER (recommended) or config.properties mint.securityAnswer for local."
        );
        return answer;
    }

    private static String buildLoginBody(String username, String password, int useBiometric) {
        return """
                {
                  "email": "%s",
                  "password": "%s",
                  "useBiometric": %d
                }
                """.formatted(escapeJson(username), escapeJson(password), useBiometric);
    }

    private static String buildPinBody(String pin) {
        return """
                {
                  "pin": "%s"
                }
                """.formatted(escapeJson(pin));
    }

    private static String build2faBody(int questionId, String answer) {
        return """
                {
                  "questionId": %d,
                  "answer": "%s"
                }
                """.formatted(questionId, escapeJson(answer));
    }

    // Minimal JSON string escape for our credential values.
    private static String escapeJson(String s) {
        if (s == null) return null;
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
