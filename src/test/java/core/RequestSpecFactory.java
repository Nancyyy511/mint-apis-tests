package core;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import utils.Config;

import java.util.Map;

public final class RequestSpecFactory {
    private RequestSpecFactory() {}

    public static RequestSpecification baseSpec() {
        RequestSpecBuilder b = new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                // Centralized logging for all requests/responses
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL));

        for (Map.Entry<String, String> e : Config.defaultHeaders().entrySet()) {
            b.addHeader(e.getKey(), e.getValue());
        }
        return b.build();
    }

    public static RequestSpecification authSpec(RequestSpecification baseSpec, String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
