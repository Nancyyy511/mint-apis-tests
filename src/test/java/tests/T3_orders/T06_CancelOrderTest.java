package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.TestDataStore;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class T06_CancelOrderTest extends C01_BaseTest {

    @Test
    void cancelOrder_success() {

        String orderId = TestDataStore.getOrderId();

        Response response = given()
                .spec(authSpec)
                .pathParam("orderId", orderId)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel");

        response.then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue())
                .body("data.status", equalTo("cancelled"));
    }
}
