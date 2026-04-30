package tests.T5_market;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import services.S5_market.S05_MarketHighlightsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class T05_MarketHighlightsTest extends C01_BaseTest {

    @Test
    void getMarketHighlights_validation() {
        Response response = S05_MarketHighlightsService.getMarketHighlights(authSpec);

        response.then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("data", notNullValue());

        List<Map<String, Object>> sections = response.jsonPath().getList("data");
        MatcherAssert.assertThat(sections.size(), equalTo(6));

        List<SectionExpectation> expected = List.of(
                new SectionExpectation("EGX30", "Gainers"),
                new SectionExpectation("EGX30", "Most Active"),
                new SectionExpectation("EGX30", "Losers"),
                new SectionExpectation("EGX70", "Gainers"),
                new SectionExpectation("EGX70", "Most Active"),
                new SectionExpectation("EGX70", "Losers")
        );

        for (SectionExpectation expectation : expected) {
            Map<String, Object> section = findSectionByKeywords(sections, expectation.market, expectation.category);
            MatcherAssert.assertThat(section, notNullValue());
            List<Map<String, Object>> stocks = getStocks(section);
            MatcherAssert.assertThat(stocks.size(), equalTo(3));
            assertStockFields(stocks);
            assertNoDuplicateTickers(stocks);
            assertOrdering(expectation.category, stocks);
        }
    }

    private Map<String, Object> findSectionByKeywords(List<Map<String, Object>> sections, String market, String category) {
        for (Map<String, Object> section : sections) {
            String name = getSectionName(section);
            if (name != null && matchesKeywords(name, market, category)) {
                return section;
            }
        }
        return null;
    }

    private String getSectionName(Map<String, Object> section) {
        Object value = section.get("sectionName");
        if (value == null) {
            value = section.get("name");
        }
        if (value == null) {
            value = section.get("title");
        }
        return value == null ? null : value.toString();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getStocks(Map<String, Object> section) {
        Object value = section.get("stocks");
        if (value == null) {
            value = section.get("data");
        }
        if (value == null) {
            value = section.get("items");
        }
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return new ArrayList<>();
    }

    private void assertStockFields(List<Map<String, Object>> stocks) {
        for (Map<String, Object> stock : stocks) {
            MatcherAssert.assertThat(stock.get("ticker"), notNullValue());
            MatcherAssert.assertThat(stock.get("price"), notNullValue());
            MatcherAssert.assertThat(stock.get("value"), notNullValue());
            MatcherAssert.assertThat(stock.get("arabicName"), notNullValue());
            MatcherAssert.assertThat(stock.get("englishName"), notNullValue());
            MatcherAssert.assertThat(stock.get("pricePercentage"), notNullValue());
            MatcherAssert.assertThat(stock.get("isPositive"), notNullValue());

            String percentage = stock.get("pricePercentage").toString();
            MatcherAssert.assertThat(percentage.endsWith("%"), equalTo(true));

            String value = stock.get("value").toString();
            MatcherAssert.assertThat(value.endsWith("M") || value.endsWith("B"), equalTo(true));

            boolean isPositive = Boolean.parseBoolean(stock.get("isPositive").toString());
            boolean percentagePositive = !percentage.trim().startsWith("-");
            MatcherAssert.assertThat(isPositive, equalTo(percentagePositive));
        }
    }

    private void assertNoDuplicateTickers(List<Map<String, Object>> stocks) {
        Set<String> seen = new HashSet<>();
        for (Map<String, Object> stock : stocks) {
            String ticker = stock.get("ticker").toString();
            MatcherAssert.assertThat(seen.contains(ticker), equalTo(false));
            seen.add(ticker);
        }
    }

    private void assertOrdering(String category, List<Map<String, Object>> stocks) {
        List<Double> percentages = new ArrayList<>();
        for (Map<String, Object> stock : stocks) {
            String percentage = stock.get("pricePercentage").toString();
            percentages.add(parsePercentage(percentage));
        }

        if (category.toLowerCase().contains("gainers")) {
            if (!allNonNegative(percentages)) {
                return;
            }
            assertSortedDescending(percentages);
        } else if (category.toLowerCase().contains("losers")) {
            if (!allNonPositive(percentages)) {
                return;
            }
            assertSortedAscending(percentages);
        }
    }

    private void assertSortedDescending(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            MatcherAssert.assertThat(values.get(i) <= values.get(i - 1), equalTo(true));
        }
    }

    private void assertSortedAscending(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            MatcherAssert.assertThat(values.get(i) >= values.get(i - 1), equalTo(true));
        }
    }

    private boolean matchesKeywords(String name, String market, String category) {
        String normalized = name.toLowerCase();
        return normalized.contains(market.toLowerCase()) && normalized.contains(category.toLowerCase());
    }

    private double parsePercentage(String percentage) {
        String sanitized = percentage.replace("%", "").replace(",", "").trim();
        return Double.parseDouble(sanitized);
    }

    private boolean allNonNegative(List<Double> values) {
        for (Double value : values) {
            if (value < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allNonPositive(List<Double> values) {
        for (Double value : values) {
            if (value > 0) {
                return false;
            }
        }
        return true;
    }

    private static class SectionExpectation {
        private final String market;
        private final String category;

        private SectionExpectation(String market, String category) {
            this.market = market;
            this.category = category;
        }
    }
}
