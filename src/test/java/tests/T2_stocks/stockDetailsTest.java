package tests.T2_stocks;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import services.S2_stocks.S02_StockService;
import utils.TestDataStore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class stockDetailsTest extends C01_BaseTest {
    @Test
    public void getStockDetails_success() {


        String ticker = "CCAP"; // later this becomes dynamic

        Response getStockDetailsresponse =
                S02_StockService.getStockDetails(authSpec , ticker);

        getStockDetailsresponse
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data.stockPriceDetails.ticker", equalTo(ticker))
                .body("data.stockPriceDetails.isinCode", notNullValue())
                .body("data.stockPriceDetails.stockId", notNullValue());

        //  Save data for next APIs
        TestDataStore.setTicker(
                getStockDetailsresponse.path("data.stockPriceDetails.ticker")
        );

        TestDataStore.setIsinCode(
                getStockDetailsresponse.path("data.stockPriceDetails.isinCode")
        );

        TestDataStore.setStockId(
                getStockDetailsresponse.path("data.stockPriceDetails.stockId")
        );
    }


}
