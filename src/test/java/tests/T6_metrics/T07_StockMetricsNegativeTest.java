package tests.T6_metrics;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import services.S6_highlight.S06_StockMetricsService;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

public class T07_StockMetricsNegativeTest extends C01_BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {"INVALID_ISIN", ""})
    void getStockMetrics_invalidIsin_shouldFail(String isinCode) {
        Response response = S06_StockMetricsService.getStockMetrics(authSpec, isinCode);

        response.then()
                .log().all()
                .statusCode(greaterThanOrEqualTo(404))
                .statusCode(lessThan(600));
    }
}
