package tests.T6_metrics;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import services.S6_highlight.S06_StockMetricsService;
import utils.TestDataStore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

public class T06_StockMetricsTest extends C01_BaseTest {

    @Test
    void getStockMetrics_success() {
        String isinCode = "EGS74801C019";

        Response response = S06_StockMetricsService.getStockMetrics(authSpec, isinCode);

        response.then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("path", notNullValue())
                .body("timeStamp", notNullValue())
                .body("data", notNullValue())
                .body("data.isinCode", equalTo(isinCode))
                .body("data.createdAt", notNullValue());

        String averageTurnover22d = response.path("data.averageTurnover22d");
        String averageVolume22d = response.path("data.averageVolume22d");

        MatcherAssert.assertThat(parsePositiveNumber(averageTurnover22d), greaterThan(0.0));
        MatcherAssert.assertThat(parsePositiveNumber(averageVolume22d), greaterThan(0.0));
    }

    private double parsePositiveNumber(String value) {
        MatcherAssert.assertThat(value, notNullValue());
        return Double.parseDouble(value);
    }
}
