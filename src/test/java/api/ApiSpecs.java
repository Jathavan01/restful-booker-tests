package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Shared request settings used by every API call.
 * Keeping them in one place means a change (like a new base URL) only happens once.
 */
public class ApiSpecs {
    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType("application/json")
                // Set as a plain header on purpose: Restful-Booker returns 418 if the
                // Accept header is anything other than exactly "application/json".
                // REST Assured's setAccept(ContentType.JSON) sends a longer list and fails.
                .addHeader("Accept", "application/json")
                .build();
    }
}