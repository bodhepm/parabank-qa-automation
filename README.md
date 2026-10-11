# ParaBank QA Automation

## Project Overview

This project automates selected banking workflows on the ParaBank demo application.

### Scenarios Covered

**Scenario A – Loan Application**
- Reset the demo database and verify API preconditions.
- Register a dynamic user.
- Open a checking account.
- Apply for a loan.
- Verify loan approval and account balance.

**Scenario B – Fund Transfers**
- Transfer $150.00, $25.50, and $8.99.
- Read transaction history.
- Calculate the total transferred amount.
- Verify the expected total of $184.49.

**Scenario C – API Validation**
- Register and log in to a customer through Postman.
- Create an account and deposit $500.
- Retrieve transaction history.
- Validate the response structure using TypeScript.

## Technologies

- Java
- Selenium WebDriver
- TestNG
- Maven
- Postman
- TypeScript
- Node.js and npm

## Prerequisites

Install Java, Maven, Node.js, npm, and Google Chrome.

## Run Java UI Tests

Open a terminal in the project root and run:

```bash
mvn test
```

Run an individual scenario:

```bash
mvn -Dtest=OpenAccountTest test
```

```bash
mvn -Dtest=TransferFundsTest test
```

## Run the TypeScript Validator

Install dependencies:

```bash
npm install
```

Set the account ID created in Scenario C:

**Windows Command Prompt**
```bat
set ACCOUNT_ID=14787
npm run validate:transactions
```

Replace `14787` with the account ID created by your latest successful Scenario C run.

## Test Results

The Java tests use TestNG. Scenario C can also be validated using the TypeScript command above. Review the console output and generated test results to confirm the outcome.

## Design Decisions

See [DECISIONS.md](DECISIONS.md) for details about shared test data, currency precision, and API/UI responsibilities.

## Implementation Note

Scenarios A and B currently use Java, Selenium, and TestNG. Scenario C uses Postman and a separate TypeScript validator. The current implementation is not a complete Playwright-based solution, and the Scenario C requests are not yet orchestrated as one fully automated TypeScript workflow.

## Limitations

ParaBank is a shared public demo environment. Database resets can affect other tests, and tests should not be run concurrently when they modify shared data.