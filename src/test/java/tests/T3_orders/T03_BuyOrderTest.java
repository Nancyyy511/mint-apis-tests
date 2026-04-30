package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import services.S3_orders.S03_OrderService;
import utils.TestDataStore;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class T03_BuyOrderTest extends C01_BaseTest {
    @Test
    void createBuyLimitOrder_success() {

        String ticker = "OIH";

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", "8221001");
        body.put("type", "buy");
        body.put("execution", "limit");
        body.put("quantity", 1);
        body.put("validity", "cancelled");
        body.put("limit", 1.22);

        Response response =
                S03_OrderService.createBuyLimitOrder(authSpec , ticker, body);

        response.then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());

        String orderId = response.path("data.OrderId");
        if (orderId == null) {
            orderId = response.path("data.orderId");
        }
        if (orderId == null) {
            orderId = response.path("data.id");
        }
        TestDataStore.setOrderId(orderId);
    }

    @Test
    void createBuyMarketOrder_success() {

        String ticker = "OIH";

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", "8221001");
        body.put("type", "buy");
        body.put("execution", "market");
        body.put("quantity", 1);

        Response response =
                S03_OrderService.createBuyMarketOrder(authSpec, ticker, body);

        response.then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());

        String orderId = response.path("data.OrderId");
        if (orderId == null) {
            orderId = response.path("data.orderId");
        }
        if (orderId == null) {
            orderId = response.path("data.id");
        }
        TestDataStore.setOrderId(orderId);
    }

}
