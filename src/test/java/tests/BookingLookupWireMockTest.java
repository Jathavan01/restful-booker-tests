package tests;

import api.BookingLookupService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import models.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Service virtualization: WireMock plays the booking API, so we can force
 * responses (404, 503) that the real public API can't produce on demand.
 */
class BookingLookupWireMockTest {

    // Starts a fake server on a free port before the tests and stops it afterward
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private static final String BOOKING_JSON = """
            {"firstname":"Mock","lastname":"Guest","totalprice":100,"depositpaid":true,
             "bookingdates":{"checkin":"2026-12-01","checkout":"2026-12-05"},
             "additionalneeds":"None"}
            """;

    private BookingLookupService service() {
        return new BookingLookupService(wireMock.baseUrl());
    }

    @Test
    void returnsBookingWhenServiceResponds() {
        wireMock.stubFor(get(urlEqualTo("/booking/1")).willReturn(okJson(BOOKING_JSON)));

        Optional<Booking> result = service().findById(1);

        assertTrue(result.isPresent());
        assertEquals("Mock", result.get().getFirstname());
    }

    @Test
    void returnsEmptyWhenBookingNotFound() {
        wireMock.stubFor(get(urlEqualTo("/booking/2")).willReturn(notFound()));

        assertTrue(service().findById(2).isEmpty());
    }

    @Test
    void retriesOnceAfterServerError() {
        // Stateful stub: the first call fails with 503, the second call succeeds
        wireMock.stubFor(get(urlEqualTo("/booking/3"))
                .inScenario("flaky service")
                .whenScenarioStateIs(STARTED)
                .willReturn(serviceUnavailable())
                .willSetStateTo("recovered"));
        wireMock.stubFor(get(urlEqualTo("/booking/3"))
                .inScenario("flaky service")
                .whenScenarioStateIs("recovered")
                .willReturn(okJson(BOOKING_JSON)));

        assertTrue(service().findById(3).isPresent());
        // Proves the retry actually happened: exactly two requests were made
        wireMock.verify(2, getRequestedFor(urlEqualTo("/booking/3")));
    }
}