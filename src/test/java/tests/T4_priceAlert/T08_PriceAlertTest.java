package tests.T4_priceAlert;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.alerts.S04_PriceAlertService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class T08_PriceAlertTest extends C01_BaseTest{


        @BeforeEach
        void cleanBefore() {
            deleteAllAlertsForStock(178);
        }

        @AfterEach
        void cleanAfter() {
            deleteAllAlertsForStock(178);
        }

        @Test
        void createAlert_success() {
            int stockId = 178;
            double targetPrice = 0.326;
            String direction = "below";

            Response beforeResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> beforeAlerts = getAlertsFromResponse(beforeResponse);

            Response createResponse = createAlert(stockId, targetPrice, direction);

            createResponse.then()
                    .log().all()
                    .statusCode(201)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue())
                    .body("data", notNullValue());

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);

            getResponse.then()
                    .log().all()
                    .statusCode(200)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue())
                    .body("data", notNullValue())
                    .body("data", not(empty()));

            MatcherAssert.assertThat(beforeAlerts.size(), equalTo(0));
            MatcherAssert.assertThat(countAlerts(alerts, targetPrice, direction), equalTo(1));
        }

        @Test
        void createAlert_duplicate_shouldFail() {
            int stockId = 178;
            double targetPrice = 0.326;
            String direction = "below";

            createAlert(stockId, targetPrice, direction).then().statusCode(201);

            Response duplicateResponse = createAlert(stockId, targetPrice, direction);

            duplicateResponse.then()
                    .log().all()
                    .statusCode(400)
                    .body("status", equalTo(false))
                    .body("message", notNullValue())
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);
            MatcherAssert.assertThat(countAlerts(alerts, targetPrice, direction), equalTo(1));
        }

        @Test
        void createAlert_maxLimit_shouldFail() {
            int stockId = 178;
            String direction = "below";

            for (int i = 0; i < 5; i++) {
                double price = 0.30 + (i * 0.01);
                createAlert(stockId, price, direction).then().statusCode(201);
            }

            Response limitResponse = createAlert(stockId, 0.40, direction);

            limitResponse.then()
                    .log().all()
                    .statusCode(400)
                    .body("status", equalTo(false))
                    .body("message", notNullValue())
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);
            MatcherAssert.assertThat(alerts.size(), equalTo(5));
        }

        @Test
        void getAlerts_validation() {
            int stockId = 178;
            double targetPrice = 0.326;
            String direction = "below";

            createAlert(stockId, targetPrice, direction).then().statusCode(201);

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);

            getResponse.then()
                    .log().all()
                    .statusCode(200)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue())
                    .body("data", notNullValue())
                    .body("data", not(empty()))
                    .body("data.direction", hasItem(direction));

            MatcherAssert.assertThat(countAlerts(alerts, targetPrice, direction), equalTo(1));
            MatcherAssert.assertThat(findAlertId(alerts, targetPrice, direction), notNullValue());
        }

        @Test
        void deleteAlert_shouldRemoveAlert() {
            int stockId = 178;
            double targetPrice = 0.326;
            String direction = "below";

            createAlert(stockId, targetPrice, direction).then().statusCode(201);

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);
            String alertId = findAlertId(alerts, targetPrice, direction);
            int beforeCount = alerts.size();

            MatcherAssert.assertThat(alertId, notNullValue());

            Response deleteResponse = S04_PriceAlertService.deletePriceAlert(authSpec, alertId);
            deleteResponse.then()
                    .log().all()
                    .statusCode(200)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue())
                    .body("data", notNullValue());

            Response afterResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> afterAlerts = getAlertsFromResponse(afterResponse);

            afterResponse.then()
                    .statusCode(200)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());

            MatcherAssert.assertThat(afterAlerts.size(), equalTo(beforeCount - 1));
            MatcherAssert.assertThat(countAlerts(afterAlerts, targetPrice, direction), equalTo(0));
        }

        @Test
        void deleteAlert_nonExisting_shouldFail() {
            String nonExistingId = "999999999";

            Response deleteResponse = S04_PriceAlertService.deletePriceAlert(authSpec, nonExistingId);

            deleteResponse.then()
                    .log().all()
                    .statusCode(404)
                    .body("status", equalTo(false))
                    .body("message", notNullValue())
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());
        }

        @Test
        void createAlert_invalidDirection_shouldFail() {
            int stockId = 178;

            Response createResponse = createAlert(stockId, 0.326, "sideways");

            createResponse.then()
                    .log().all()
                    .statusCode(400)
                    .body("status", equalTo(false))
                    .body("message", notNullValue())
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());
        }

        @Test
        void createAlert_invalidTargetPrice_shouldFail() {
            int stockId = 178;

            Response createResponse = createAlert(stockId, 0.0, "below");

            createResponse.then()
                    .log().all()
                    .statusCode(400)
                    .body("status", equalTo(false))
                    .body("message", notNullValue())
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue());
        }

        @Test
        void getAlerts_whenNoneExist_shouldReturnEmptyList() {
            int stockId = 178;

            Response getResponse = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = getAlertsFromResponse(getResponse);

            getResponse.then()
                    .log().all()
                    .statusCode(200)
                    .body("status", equalTo(true))
                    .body("message", equalTo("Operation Succeed"))
                    .body("path", notNullValue())
                    .body("timeStamp", notNullValue())
                    .body("data", notNullValue())
                    .body("data", empty());

            MatcherAssert.assertThat(alerts.size(), equalTo(0));
        }

        private Response createAlert(int stockId, double targetPrice, String direction) {
            Map<String, Object> body = new HashMap<>();
            body.put("stockId", stockId);
            body.put("targetPrice", targetPrice);
            body.put("direction", direction);
            return S04_PriceAlertService.createPriceAlert(authSpec, body);
        }

        private List<Map<String, Object>> getAlertsFromResponse(Response response) {
            List<Map<String, Object>> alerts = response.jsonPath().getList("data");
            if (alerts == null) {
                return List.of();
            }
            return alerts;
        }

        private int countAlerts(List<Map<String, Object>> alerts, double targetPrice, String direction) {
            int count = 0;
            for (Map<String, Object> alert : alerts) {
                if (direction.equalsIgnoreCase(getStringValue(alert.get("direction")))
                        && priceMatches(alert.get("targetPrice"), targetPrice)) {
                    count++;
                }
            }
            return count;
        }

        private String findAlertId(List<Map<String, Object>> alerts, double targetPrice, String direction) {
            for (Map<String, Object> alert : alerts) {
                if (direction.equalsIgnoreCase(getStringValue(alert.get("direction")))
                        && priceMatches(alert.get("targetPrice"), targetPrice)) {
                    return getStringValue(extractAlertId(alert));
                }
            }
            return null;
        }

        private Object extractAlertId(Map<String, Object> alert) {
            Object id = alert.get("priceAlertId");
            if (id == null) {
                id = alert.get("id");
            }
            return id;
        }

        private boolean priceMatches(Object actualPrice, double expectedPrice) {
            if (actualPrice == null) {
                return false;
            }
            double actual;
            if (actualPrice instanceof Number) {
                actual = ((Number) actualPrice).doubleValue();
            } else {
                actual = Double.parseDouble(actualPrice.toString());
            }
            return Math.abs(actual - expectedPrice) < 0.000001;
        }

        private String getStringValue(Object value) {
            return value == null ? null : value.toString();
        }

        private void deleteAllAlertsForStock(int stockId) {
            Response response = S04_PriceAlertService.getPriceAlerts(authSpec, stockId);
            List<Map<String, Object>> alerts = response.jsonPath().getList("data");
            if (alerts == null) {
                return;
            }
            for (Map<String, Object> alert : alerts) {
                String alertId = getStringValue(extractAlertId(alert));
                if (alertId != null) {
                    S04_PriceAlertService.deletePriceAlert(authSpec, alertId);
                }
            }
        }
    }


