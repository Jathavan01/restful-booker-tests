Feature: Booking management
  As a hotel front-desk user
  I want to create, change, and remove bookings
  So that reservations stay accurate

  Scenario: Create a booking and read it back
    Given I have a new booking for "Alice" "Smith"
    When I create the booking
    Then the booking is created successfully
    And fetching the booking returns first name "Alice"

  Scenario: Update a booking's price
    Given an existing booking for "Bob" "Jones"
    When I update the total price to 500
    Then the booking shows a total price of 500

  Scenario: Delete a booking
    Given an existing booking for "Cara" "Lee"
    When I delete the booking
    Then the booking can no longer be found

  Scenario: Cannot delete without logging in
    Given an existing booking for "Dan" "Kim"
    When I try to delete the booking without logging in
    Then the request is forbidden