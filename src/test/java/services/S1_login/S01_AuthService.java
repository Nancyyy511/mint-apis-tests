package services.S1_login;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S01_AuthService {

    public static Response login(RequestSpecification baseSpec , String body) {
        return given()
                .spec(baseSpec)
                .body(body)
                .when()
                .post("/api/v1/auth/user/login");

    }

    public static Response verifyPin(RequestSpecification baseSpec , String body, String guestToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + guestToken)
                .body(body)
                .when()
                .post("/api/v1/auth/user/verify-pin");
    }

    public static Response get2faQuestions(RequestSpecification baseSpec, String guestToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + guestToken)
                .when()
                .get("/api/v1/auth/user/2fa/my-questions");
    }

    public static Response validate2faQuestion(RequestSpecification baseSpec, String body, String guestToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + guestToken)
                .body(body)
                .when()
                .post("/api/v1/auth/user/2fa/validate-question");
    }

    public static Response refreshToken(RequestSpecification baseSpec, String refreshToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + refreshToken)
                .when()
                .post("/api/v1/auth/user/refresh-token");
    }

    public static Response logout(RequestSpecification baseSpec, String token) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/auth/user/logout");
    }
}
