
package com.immverse.qa.tests;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.immverse.qa.api.ParaBankApiClient;
import com.immverse.qa.pages.OpenAccountPage;
import com.immverse.qa.pages.RegisterPage;
import com.immverse.qa.pages.TransferFundsPage;
import com.immverse.qa.utils.TestDataGenerator;

public class TransferFundsTest extends BaseTest {

    @Test
    public void verifyThreeTransfersTotal() throws Exception {

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Reset the database through the API.
        int statusCode = ParaBankApiClient.cleanDatabase();

        Assert.assertEquals(
                statusCode,
                204,
                "Database reset API should return HTTP 204"
        );

        // Step 2: Register a unique user.
        RegisterPage registerPage = new RegisterPage(driver);

        String username = TestDataGenerator.generateUsername();

        registerPage.openRegistrationPage();

        registerPage.registerUser(
                "QA",
                "Transfer",
                "Test Address",
                "Pune",
                "Maharashtra",
                "411001",
                "9876543210",
                "123456789",
                username,
                "Test@12345"
        );

        String registrationMessage =
                registerPage.getRegistrationMessage();

        Assert.assertTrue(
                registrationMessage.contains("Welcome"),
                "Registration failed: " + registrationMessage
        );

        System.out.println("Registered user: " + username);

        // Step 3: Open a second Checking account.
        OpenAccountPage accountPage = new OpenAccountPage(driver);

        accountPage.openNewAccountPage();
        accountPage.selectCheckingAccount();
        accountPage.selectSourceAccount();
        accountPage.submitAccountRequest();

        String secondAccount = accountPage.getNewAccountNumber();

        Assert.assertNotNull(
                secondAccount,
                "Second account number should not be null"
        );

        // Step 4: Identify the source and destination accounts.
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Transfer Funds")
                )
        ).click();

        By fromDropdown = By.id("fromAccountId");
        By toDropdown = By.id("toAccountId");

        wait.until(d ->
                !d.findElements(fromDropdown).isEmpty()
                && !d.findElements(toDropdown).isEmpty()
        );

        wait.until(d ->
                d.findElements(fromDropdown).get(0)
                        .findElements(By.tagName("option")).size() >= 2
        );

        List<WebElement> accountOptions =
                driver.findElements(fromDropdown).get(0)
                        .findElements(By.tagName("option"));

        String firstAccount =
                accountOptions.get(0).getText().trim();

        secondAccount =
                accountOptions.get(1).getText().trim();

        Assert.assertNotEquals(
                firstAccount,
                secondAccount,
                "Source and destination accounts must be different"
        );

        System.out.println("Source account: " + firstAccount);
        System.out.println("Destination account: " + secondAccount);

        // Step 5: Calculate outgoing transfers BEFORE our transfers.
        BigDecimal totalBefore =
                getTotalFundsTransferSentForToday(wait, firstAccount);

        System.out.println(
                "Total before our transfers: $" + totalBefore
        );

        // Return to the Transfer Funds page.
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Transfer Funds")
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("amount")
                )
        );

        // Step 6: Perform the three required transfers.
        TransferFundsPage transferPage =
                new TransferFundsPage(driver);

        String[] amounts = {"150.00", "25.50", "8.99"};

        for (String amount : amounts) {

            transferPage.transferFunds(
                    amount,
                    firstAccount,
                    secondAccount
            );

            String result = transferPage.getTransferResult();

            Assert.assertTrue(
                    result.contains("Transfer Complete"),
                    "Transfer failed for $" + amount
                            + ". Result: " + result
            );

            System.out.println(
                    "Transfer successful: $" + amount
            );

            // Return to Transfer Funds for the next transfer.
            if (!amount.equals("8.99")) {
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.linkText("Transfer Funds")
                        )
                ).click();
            }
        }

        System.out.println("All three transfers completed.");

        // Step 7: Calculate outgoing transfers AFTER our transfers.
        BigDecimal totalAfter =
                getTotalFundsTransferSentForToday(wait, firstAccount);

        System.out.println(
                "Total after our transfers: $" + totalAfter
        );

        // Step 8: Subtract the old total from the new total.
        BigDecimal actualNewTransfersTotal =
                totalAfter.subtract(totalBefore);

        System.out.println(
                "Total of our three transfers: $"
                        + actualNewTransfersTotal
        );

        BigDecimal expectedTotal = new BigDecimal("184.49");

        Assert.assertEquals(
                actualNewTransfersTotal.compareTo(expectedTotal),
                0,
                "The new transfers total should be $184.49"
        );

        System.out.println(
                "PASS: New transfers total is $184.49"
        );
    }

    // Reads today's outgoing transfer amounts from the actual table.
    private BigDecimal getTotalFundsTransferSentForToday(
            WebDriverWait wait,
            String accountNumber) {

        // Open Find Transactions.
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Find Transactions")
                )
        ).click();

        // Select the source account.
        By accountDropdown = By.id("accountId");

        WebElement accountSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                        accountDropdown
                )
        );

        new Select(accountSelect)
                .selectByVisibleText(accountNumber);

        // Search for today's transactions.
        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("MM-dd-yyyy")
        );

        By dateField = By.id("transactionDate");

        WebElement dateInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        dateField
                )
        );

        dateInput.clear();
        dateInput.sendKeys(today);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("findByDate")
                )
        ).click();

        // Read the transaction table.
        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("transactionTable")
                )
        );

        List<WebElement> headers =
                table.findElements(
                        By.cssSelector("thead th")
                );

        int debitColumnIndex = -1;

        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains("Debit")) {
                debitColumnIndex = i;
                break;
            }
        }

        Assert.assertTrue(
                debitColumnIndex >= 0,
                "Debit column was not found in the transaction table"
        );

        List<WebElement> rows =
                table.findElements(
                        By.cssSelector("tbody tr")
                );

        BigDecimal total = BigDecimal.ZERO;

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(By.tagName("td"));

            if (cells.size() <= debitColumnIndex || cells.size() < 2) {
                continue;
            }

            String transactionName =
                    cells.get(1).getText().trim();

            String debitText =
                    cells.get(debitColumnIndex).getText().trim();

            if ("Funds Transfer Sent".equals(transactionName)
                    && !debitText.isEmpty()) {

                // Remove $, commas, and other currency formatting.
                String numericAmount =
                        debitText.replaceAll("[^0-9.]", "");

                if (!numericAmount.isEmpty()) {
                    total = total.add(
                            new BigDecimal(numericAmount)
                    );
                }
            }
        }

        return total;
    }
}
