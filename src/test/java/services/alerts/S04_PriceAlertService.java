package services.alerts;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class S04_PriceAlertService {

    public static Response createPriceAlert(RequestSpecification authSpec, Object body) {
        return given()
                .spec(authSpec)
                .body(body)
                .when()
                .post("/api/v1/price-alerts");
    }

    public static Response getPriceAlerts(RequestSpecification authSpec, int stockId) {
        return given()
                .spec(authSpec)
                .pathParam("stockId", stockId)
                .when()
                .get("/api/v1/price-alerts/{stockId}");
    }

    public static Response deletePriceAlert(RequestSpecification authSpec, String priceAlertId) {
        return given()
                .spec(authSpec)
                .pathParam("priceAlertId", priceAlertId)
                .when()
                .delete("/api/v1/price-alerts/{priceAlertId}");
    }
}
