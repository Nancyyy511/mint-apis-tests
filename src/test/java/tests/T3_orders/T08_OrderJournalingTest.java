package tests.T3_orders;

import core.C01_BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import services.S7_jounaling.S07_OrderJournalingService;
import utils.ErrorResponseMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T08_OrderJournalingTest extends C01_BaseTest {

    @Test
    void createOrderJournal_success_minimal() {
        int orderId = 60086012;
        Map<String, Object> body = baseBody(orderId, true);
        body.put("influence", "TECHNICAL");
        body.put("note", "Expect a strong breakout after consolidation");

        Response response = S07_OrderJournalingService.createOrderJournal(authSpec, body);

        assertSuccessEnvelope(response, 201);
        response.then()
                .body("data", notNullValue())
                .body("data.orderId", equalTo(60086012))
                .body("data.confidence", equalTo(true))
                .body("data.influence", equalTo("TECHNICAL"))
                .body("data.note", equalTo("Expect a strong breakout after consolidation"));
    }

    @Test
    void createOrderJournal_success_full() {
        int orderId = generateOrderId();
        String note = repeat("A", 500);
        Map<String, Object> body = baseBody(orderId, false);
        body.put("influence", "TECHNICAL");
        body.put("note", note);

        Response response = S07_OrderJournalingService.createOrderJournal(authSpec, body);

        assertSuccessEnvelope(response, 201);
        response.then()
                .body("data", notNullValue());
    }

    @Test
    void createOrderJournal_duplicate_shouldFail() {
        int orderId = generateOrderId();
        Map<String, Object> body = baseBody(orderId, true);

        S07_OrderJournalingService.createOrderJournal(authSpec, body)
                .then()
                .statusCode(201);

        Response duplicate = S07_OrderJournalingService.createOrderJournal(authSpec, body);
        ErrorResponseMatcher.assertErrorResponse(duplicate, 409);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void createOrderJournal_invalid_shouldFail(Map<String, Object> body, int expectedStatusCode) {
        Response response = S07_OrderJournalingService.createOrderJournal(authSpec, body);
        ErrorResponseMatcher.assertErrorResponse(response, expectedStatusCode);
    }

    @Disabled("Known backend 500 bug on GET /api/v1/order-journaling/{orderId}")
    @Test
    void getOrderJournal_shouldSucceed() {
        int orderId = generateOrderId();
        Map<String, Object> body = baseBody(orderId, true);
        S07_OrderJournalingService.createOrderJournal(authSpec, body)
                .then()
                .statusCode(201);

        Response response = S07_OrderJournalingService.getOrderJournal(authSpec, orderId);

        assertSuccessEnvelope(response, 200);
        response.then()
                .body("data", notNullValue());
    }

    private Stream<org.junit.jupiter.params.provider.Arguments> invalidRequests() {
        int orderId = generateOrderId();
        Map<String, Object> missingConfidence = baseBody(orderId, true);
        missingConfidence.remove("confidence");

        Map<String, Object> nonBooleanConfidence = baseBody(orderId, true);
        nonBooleanConfidence.put("confidence", "true");

        Map<String, Object> invalidInfluence = baseBody(orderId, true);
        invalidInfluence.put("influence", "MOMENTUM");

        Map<String, Object> longNote = baseBody(orderId, true);
        longNote.put("note", repeat("B", 501));

        Map<String, Object> missingOrderId = baseBody(orderId, true);
        missingOrderId.remove("orderId");

        Map<String, Object> emptyOrderId = baseBody(orderId, true);
        emptyOrderId.put("orderId", "");

        Map<String, Object> stringOrderId = baseBody(orderId, true);
        stringOrderId.put("orderId", "ABC123");

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(missingConfidence, 400),
                org.junit.jupiter.params.provider.Arguments.of(nonBooleanConfidence, 400),
                org.junit.jupiter.params.provider.Arguments.of(invalidInfluence, 400),
                org.junit.jupiter.params.provider.Arguments.of(longNote, 400),
                org.junit.jupiter.params.provider.Arguments.of(missingOrderId, 400),
                org.junit.jupiter.params.provider.Arguments.of(emptyOrderId, 400),
                org.junit.jupiter.params.provider.Arguments.of(stringOrderId, 400)
        );
    }

    private Map<String, Object> baseBody(int orderId, boolean confidence) {
        Map<String, Object> body = new HashMap<>();
        body.put("orderId", orderId);
        body.put("confidence", confidence);
        return body;
    }

    private void assertSuccessEnvelope(Response response, int expectedStatusCode) {
        response.then()
                .log().all()
                .statusCode(expectedStatusCode)
                .body("status", equalTo(true))
                .body("message", equalTo("Operation Succeed"))
                .body("path", instanceOf(String.class))
                .body("timeStamp", instanceOf(String.class))
                .body("data", notNullValue());
    }

    private String repeat(String value, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }

    private int generateOrderId() {
        return (int) (System.currentTimeMillis() & 0x7fffffff);
    }
}
