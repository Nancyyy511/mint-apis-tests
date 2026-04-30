package services.S7_jounaling;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S07_OrderJournalingService {

    public static Response createOrderJournal(RequestSpecification authSpec, Object body) {
        return given()
                .spec(authSpec)
                .body(body)
                .when()
                .post("/api/v1/order-journaling");
    }

    public static Response getOrderJournal(RequestSpecification authSpec, int orderId) {
        return given()
                .spec(authSpec)
                .pathParam("orderId", orderId)
                .when()
                .get("/api/v1/order-journaling/{orderId}");
    }
}
