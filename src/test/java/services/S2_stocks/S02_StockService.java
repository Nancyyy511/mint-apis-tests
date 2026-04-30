package services.S2_stocks;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S02_StockService {

    public static Response getStockDetails(RequestSpecification authSpec, String ticker) {
        return given()
                .spec(authSpec)
                .pathParam("ticker", ticker)
                .when()
                .get("/api/v1/stocks/{ticker}");

    }

}