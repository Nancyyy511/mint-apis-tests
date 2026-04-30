package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import services.S3_orders.S03_OrderService;
import utils.TestDataStore;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class T04_SellOrderTes extends C01_BaseTest {

    @Test
    void createSellMarketOrder_success() {

        String ticker = TestDataStore.getTicker();

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", "8221001");
        body.put("type", "sell");
        body.put("execution", "market");
        body.put("quantity", 1);

        Response response =
                S03_OrderService.createSellMarketOrder(authSpec, ticker, body);

        response.then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());
    }

    @Test
    void createSellLimitOrder_success() {

        String ticker = TestDataStore.getTicker();

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", "8221001");
        body.put("type", "sell");
        body.put("execution", "limit");
        body.put("quantity", 1);
        body.put("validity", "cancelled");
        body.put("limit", 1.22);

        Response response =
                S03_OrderService.createSellLimitOrder(authSpec, ticker, body);

        response.then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());
    }

    @Test
    void createSellOrder_quantityExceedsOwnedShares() {

        String ticker = TestDataStore.getTicker();

        Map<String, Object> body = new HashMap<>();
        body.put("accountId", "8221001");
        body.put("type", "sell");
        body.put("execution", "limit");
        body.put("quantity", 999999);
        body.put("validity", "cancelled");
        body.put("limit", 1.22);

        Response response =
                S03_OrderService.createSellLimitOrder(authSpec, ticker, body);

        response.then()
                .log().all()
                .statusCode(400)
                .body("status", equalTo(false))
                .body("message", containsString("share"));
    }
}
