package services.S5_market;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S05_MarketHighlightsService {

    public static Response getMarketHighlights(RequestSpecification authSpec) {
        return given()
                .spec(authSpec)
                .when()
                .get("/api/v1/market-highlights");
    }
}
