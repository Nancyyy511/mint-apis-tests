package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.TestDataStore;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class T07_OrderHistoryTest extends C01_BaseTest {

    @Test
    void getOrderHistory_containsCreatedOrder() {

        String orderId = TestDataStore.getOrderId();

        Response response = given()
                .spec(authSpec)
                .when()
                .get("/api/v1/orders/history");

        response.then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("data", notNullValue())
                .body("data", not(empty()))
                .body("data.orderId", hasItem(orderId));
    }
}
