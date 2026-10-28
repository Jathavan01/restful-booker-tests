package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecs {
    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType("application/json")
                .addHeader("Accept", "application/json")
                .build();
    }
}