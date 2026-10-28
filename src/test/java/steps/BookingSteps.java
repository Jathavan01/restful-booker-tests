package steps;

import api.AuthClient;
import api.BookingClient;
import api.TestData;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.Booking;
import models.CreateBookingResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Connects each plain-English line in booking.feature to code.
 * Cucumber creates a new instance of this class for every scenario, so the
 * fields below hold that scenario's state and scenarios can't affect each other.
 * The steps reuse the same API clients as the JUnit tests.
 */
public class BookingSteps {

    private String token;
    private Booking booking;
    private Integer bookingId;
    private Response lastResponse;   // the most recent API response, checked by the Then steps

    // Runs before every scenario: log in once so steps can use the token
    @Before
    public void login() {
        token = AuthClient.getToken();
    }

    // Runs after every scenario: delete the booking so the shared API isn't left full of test data.
    // If the scenario already deleted it, this second delete is harmless because we don't check the result.
    @After
    public void cleanUp() {
        if (bookingId != null) {
            BookingClient.delete(bookingId, token);
        }
    }

    @Given("I have a new booking for {string} {string}")
    public void iHaveANewBookingFor(String firstName, String lastName) {
        booking = TestData.newBooking();
        booking.setFirstname(firstName);
        booking.setLastname(lastName);
    }

    @Given("an existing booking for {string} {string}")
    public void anExistingBookingFor(String firstName, String lastName) {
        iHaveANewBookingFor(firstName, lastName);
        bookingId = BookingClient.create(booking).as(CreateBookingResponse.class).getBookingid();
    }

    @When("I create the booking")
    public void iCreateTheBooking() {
        lastResponse = BookingClient.create(booking);
        if (lastResponse.statusCode() == 200) {
            bookingId = lastResponse.as(CreateBookingResponse.class).getBookingid();
        }
    }

    @When("I update the total price to {int}")
    public void iUpdateTheTotalPriceTo(int price) {
        booking.setTotalprice(price);
        lastResponse = BookingClient.update(bookingId, booking, token);
    }

    @When("I delete the booking")
    public void iDeleteTheBooking() {
        lastResponse = BookingClient.delete(bookingId, token);
    }

    @When("I try to delete the booking without logging in")
    public void iTryToDeleteTheBookingWithoutLoggingIn() {
        lastResponse = BookingClient.deleteWithoutToken(bookingId);
    }

    @Then("the booking is created successfully")
    public void theBookingIsCreatedSuccessfully() {
        lastResponse.then().statusCode(200);
        assertNotNull(bookingId);
    }

    @Then("fetching the booking returns first name {string}")
    public void fetchingTheBookingReturnsFirstName(String expected) {
        Booking fetched = BookingClient.get(bookingId)
                .then().statusCode(200)
                .extract().as(Booking.class);
        assertEquals(expected, fetched.getFirstname());
    }

    @Then("the booking shows a total price of {int}")
    public void theBookingShowsATotalPriceOf(int expected) {
        Booking result = lastResponse.then().statusCode(200)
                .extract().as(Booking.class);
        assertEquals(expected, result.getTotalprice());
    }

    @Then("the booking can no longer be found")
    public void theBookingCanNoLongerBeFound() {
        BookingClient.get(bookingId).then().statusCode(404);
    }

    @Then("the request is forbidden")
    public void theRequestIsForbidden() {
        lastResponse.then().statusCode(403);
    }
}