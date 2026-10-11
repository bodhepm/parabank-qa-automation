
import type { Transaction } from "./Transaction";


function isTransaction(value: unknown): value is Transaction {
    if (typeof value !== "object" || value === null) {
        return false;
    }

    const transaction = value as Record<string, unknown>;

    const expectedFields = [
        "id",
        "accountId",
        "type",
        "date",
        "amount",
        "description"
    ];

    const actualFields = Object.keys(transaction);

    if (
        actualFields.length !== expectedFields.length ||
        !expectedFields.every(field => actualFields.includes(field))
    ) {
        return false;
    }

    return (
        typeof transaction.id === "number" &&
        typeof transaction.accountId === "number" &&
        typeof transaction.type === "string" &&
        typeof transaction.date === "number" &&
        typeof transaction.amount === "number" &&
        typeof transaction.description === "string"
    );
}


async function main(): Promise<void> {
    const baseUrl = "https://parabank.parasoft.com/parabank";
    const accountId = process.env.ACCOUNT_ID;

    if (!accountId) {
        throw new Error("Please set the ACCOUNT_ID environment variable.");
    }

    const url =
        `${baseUrl}/services/bank/accounts/${accountId}/transactions`;

    const response = await fetch(url, {
        headers: { Accept: "application/json" }
    });

    if (!response.ok) {
        throw new Error(`API returned HTTP ${response.status}`);
    }

    const data: unknown = await response.json();

    if (!Array.isArray(data) || !data.every(isTransaction)) {
        throw new Error("Transaction response does not match the interface.");
    }

    const transactions: Transaction[] = data;

    if (!transactions.some(
        transaction =>
            transaction.accountId === Number(accountId) &&
            transaction.description === "Deposit via Web Service" &&
            transaction.amount === 500
    )) {
        throw new Error("Expected $500 deposit was not found.");
    }

    console.log("PASS: Transaction structure is valid.");
    console.log(`PASS: Found ${transactions.length} transactions.`);
    console.log("PASS: Expected $500 deposit was found.");
}

main().catch(error => {
    console.error("FAIL:", error.message);
    process.exitCode = 1;
});
