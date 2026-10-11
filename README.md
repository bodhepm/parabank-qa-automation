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

## Running Scenario C with Postman

1. Install Postman.
2. Download or clone this repository.
3. Import `postman/ParaBank_Automation.postman_collection.json` into Postman.
4. Create or select an environment with the `baseUrl` variable set to `https://parabank.parasoft.com/parabank`.
5. Run the requests in the **Scenario C - API Validation** folder in order, from `00 - Open Registration Page` through `06 - Get Transaction History`.
6. Check the Postman test results after each request.

The registration request creates a new test user. Later requests use the customer and account IDs captured from earlier responses. The collection uses the shared public ParaBank demo environment, so avoid running database-reset requests unnecessarily.

### TypeScript Transaction Validation

From the project root, install dependencies:

```bash
npm install
```

Set `ACCOUNT_ID` to the account created by Scenario C, then run:

**Windows Command Prompt**

```bat
set ACCOUNT_ID=YOUR_ACCOUNT_ID
npm run validate:transactions
```

Replace `YOUR_ACCOUNT_ID` with the actual account ID. The TypeScript validator independently checks the transaction API response; it does not automatically execute the Postman collection.