package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.TestDataStore;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class T05_UpdateOrderTest extends C01_BaseTest {

    @Test
    void updateOrderQuantityOrLimit_success() {

        String orderId = TestDataStore.getOrderId();

        Map<String, Object> body = new HashMap<>();
        body.put("quantity", 1);
        body.put("limit", 1.25);
        body.put("execution", "limit");
        body.put("validity", "cancelled");


        Response response = given()
                .spec(authSpec)
                .pathParam("orderId", 50276305)
                .body(body)
                .when()
                .patch("/api/v1/orders/{orderId}/update");

        response.then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());
    }
}
