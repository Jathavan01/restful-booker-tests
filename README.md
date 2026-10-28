# Restful-Booker API Test Automation

![CI](https://github.com/Jathavan01/restful-booker-tests/actions/workflows/ci.yml/badge.svg)

Java API test automation framework for [Restful-Booker](https://restful-booker.herokuapp.com), a public hotel booking API built for practicing testing. Tests run automatically on every push with GitHub Actions.

## Tech stack

- Java 17, Maven
- JUnit 5 (test runner)
- REST Assured (API requests and assertions)
- Jackson (JSON to Java objects)
- GitHub Actions (CI)

## What is covered (12 tests)

- **Smoke:** health check and login return the expected responses
- **Create and read:** a created booking comes back with the saved details and can be fetched by ID
- **Update and delete:** full update (PUT), partial update (PATCH), and delete followed by a check that the booking is gone
- **Negative and security:** update or delete without a token, update with an invalid token, a non-existent booking, and a failed login

Every test creates its own booking and cleans up afterward, because the API is shared and its data can change.

## Project structure

```
src/test/java
├── api/      request settings, one client class per endpoint group, test data
├── models/   Java classes for the booking JSON
└── tests/    the test classes
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

- Cucumber (BDD) scenarios
- Allure test reports