package utils;

import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ErrorResponseMatcher {

    public static void assertErrorResponse(Response response, int expectedStatusCode) {
        response.then()
                .log().all()
                .statusCode(expectedStatusCode)
                .body("status", equalTo(false))
                .body("message", notNullValue())
                .body("path", instanceOf(String.class))
                .body("timeStamp", instanceOf(String.class));
    }
}
