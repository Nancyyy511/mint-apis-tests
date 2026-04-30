package services.S3_orders;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S03_OrderService {

    public static Response createBuyLimitOrder(RequestSpecification authSpec, String ticker, Object body) {
        return given()
                .spec(authSpec)
                .pathParam("ticker", ticker)
                .log().headers()
                .body(body)
                .when()
                .post("/api/v1/orders/{ticker}");

    }

    public static Response createBuyMarketOrder(RequestSpecification authSpec, String ticker, Object body) {
        return given()
                .spec(authSpec)
                .pathParam("ticker", ticker)
                .log().headers()
                .body(body)
                .when()
                .post("/api/v1/orders/{ticker}");

    }

    public static Response createSellLimitOrder(RequestSpecification authSpec, String ticker, Object body) {
        return given()
                .spec(authSpec)
                .pathParam("ticker", ticker)
                .log().headers()
                .body(body)
                .when()
                .post("/api/v1/orders/{ticker}");

    }

    public static Response createSellMarketOrder(RequestSpecification authSpec, String ticker, Object body) {
        return given()
                .spec(authSpec)
                .pathParam("ticker", ticker)
                .log().headers()
                .body(body)
                .when()
                .post("/api/v1/orders/{ticker}");

    }

}
