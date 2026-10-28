# Restful-Booker API Test Automation

![CI](https://github.com/Jathavan01/restful-booker-tests/actions/workflows/ci.yml/badge.svg)

Java API test automation framework for [Restful-Booker](https://restful-booker.herokuapp.com), a public hotel booking API built for practicing testing. Tests run automatically on every push with GitHub Actions.

## Tech stack

- Java 17, Maven
- JUnit 5 (test runner)
- REST Assured (API requests and assertions)
- JSON Schema Validator (response contract checks)
- Jackson (JSON to Java objects)
- Cucumber (BDD scenarios in plain English)
- GitHub Actions (CI)

## What is covered (14 JUnit tests and 4 Cucumber scenarios)

- **Smoke:** health check and login return the expected responses
- **Create and read:** a created booking comes back with the saved details and can be fetched by ID
- **Update and delete:** full update (PUT), partial update (PATCH), and delete followed by a check that the booking is gone
- **Negative and security:** update or delete without a token, update with an invalid token, a non-existent booking, and a failed login
- **Contract tests:** create and get responses are checked against JSON schemas for required fields and data types
- **BDD scenarios:** create and read back, update a price, delete, and a forbidden delete without logging in, written as Given/When/Then in `booking.feature`

Every test creates its own booking and cleans up afterward, because the API is shared and its data can change.

## Project structure

```
src/test
├── java
│   ├── api/      request settings, one client class per endpoint group, test data
│   ├── models/   Java classes for the booking JSON
│   ├── tests/    the JUnit test classes
│   ├── steps/    Cucumber step definitions
│   └── runner/   Cucumber runner (finds the feature files)
└── resources
    ├── features/ Cucumber feature files
    └── schemas/  JSON schemas for response checks
```

## How to run

```
mvn test
```

Requires Java 17 and Maven.

## API behaviors found while testing

- `GET /ping` returns 201 instead of the more typical 200
- A successful `DELETE` returns 201
- A failed login returns 200 with a "Bad credentials" message instead of an error status
- Requests are rejected with 418 unless the `Accept` header is exactly `application/json`

## Planned

- Allure test reports