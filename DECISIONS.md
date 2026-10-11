# Technical Decisions

## 1. Shared State and Parallel Execution

ParaBank uses a shared demo environment. Tests can affect the same database, so running tests in parallel may cause conflicts.

To reduce conflicts:
- Generate a unique username for each test run.
- Capture customer IDs and account IDs dynamically from API responses or the UI.
- Avoid hardcoding generated IDs.
- Do not run database reset operations in parallel with other tests.

For a more reliable CI setup, tests that reset shared data should run separately or use isolated test environments.

## 2. Currency Precision

Money calculations should use `BigDecimal` in Java to avoid floating-point calculation errors.

The expected transfer total is:

- $150.00
- $25.50
- $8.99
- **Total: $184.49**

In integer cents, the same calculation is 15000 + 2550 + 899 = 18449 cents.

## 3. API Setup, UI Testing, and Cleanup

API calls are used for setup and data retrieval where supported. Selenium UI tests validate the user-facing workflows.

- Scenario A: API preconditions followed by UI registration, account opening, and loan validation.
- Scenario B: UI transfers followed by transaction-history validation.
- Scenario C: Postman requests and a TypeScript validator check transaction API responses.

The current implementation uses Java, Selenium, and TestNG for Scenarios A and B, and Postman with a separate TypeScript validator for Scenario C. Scenario C is not yet a single fully automated end-to-end TypeScript workflow; the account ID is supplied through an environment variable.

The public ParaBank demo environment is shared, so destructive resets and parallel execution require care.