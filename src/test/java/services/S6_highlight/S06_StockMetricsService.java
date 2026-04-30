package services.S6_highlight;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S06_StockMetricsService {

    public static Response getStockMetrics(RequestSpecification authSpec, String isinCode) {
        return given()
                .spec(authSpec)
                .pathParam("isinCode", isinCode)
                .when()
                .get("/api/v1/stock-metrics/{isinCode}");
    }
}
