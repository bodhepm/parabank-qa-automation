
package com.immverse.qa.tests;

import com.immverse.qa.api.ParaBankApiClient;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.immverse.qa.pages.RegisterPage;
import com.immverse.qa.pages.OpenAccountPage;
import com.immverse.qa.pages.RequestLoanPage;
import com.immverse.qa.utils.TestDataGenerator;

public class OpenAccountTest extends BaseTest {

	
	@Test
	public void verifyCheckingAccountCanBeOpened() {

		// Step 0: API setup before UI actions
		try {
		    // Reset the database through the API
		    int statusCode = ParaBankApiClient.cleanDatabase();

		    Assert.assertEquals(
		            statusCode,
		            204,
		            "Database reset API should return HTTP 204"
		    );

		    System.out.println(
		            "PASS: Database reset API returned HTTP " + statusCode
		    );

		    // Verify Loan Provider configuration
		    String adminResponse =
		            ParaBankApiClient.getLoanProviderSettings();

		    Assert.assertTrue(
		            adminResponse.contains(
		                    "<option value=\"ws\" selected=\"selected\">Web Service"),
		            "Loan Provider must be configured as Web Service before UI testing"
		    );

		    System.out.println(
		            "PASS: API preconditions verified before UI test."
		    );

		} catch (Exception e) {
		    Assert.fail(
		            "API setup failed: " + e.getMessage(),
		            e
		    );
		}
		

	    WebDriverWait wait =
	            new WebDriverWait(driver, Duration.ofSeconds(15));
	

        // Step 1: Register a fresh user
        RegisterPage registerPage = new RegisterPage(driver);

        String username = TestDataGenerator.generateUsername();

        registerPage.openRegistrationPage();

        registerPage.registerUser(
                "QA",
                "Automation",
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

        System.out.println("Registered username: " + username);

        // Step 2: Open a Checking account
        OpenAccountPage accountPage = new OpenAccountPage(driver);

        accountPage.openNewAccountPage();
        accountPage.selectCheckingAccount();
        accountPage.selectSourceAccount();
        accountPage.submitAccountRequest();

        String accountNumber = accountPage.getNewAccountNumber();

        Assert.assertNotNull(
                accountNumber,
                "Checking account number should not be null"
        );

        Assert.assertFalse(
                accountNumber.trim().isEmpty(),
                "Checking account number should not be empty"
        );

        System.out.println(
                "New Checking account number: " + accountNumber
        );

        // Step 3: Apply for a loan using the new Checking account
        RequestLoanPage loanPage = new RequestLoanPage(driver);

        loanPage.openLoanPage();
        loanPage.applyForLoan("200", "100", accountNumber);

        String loanResult = loanPage.getLoanResult();

        System.out.println("Loan application result:");
        System.out.println(loanResult);

        Assert.assertTrue(
                loanResult.contains("Loan Request Processed"),
                "Loan request was not processed: " + loanResult
        );

        Assert.assertTrue(
                loanResult.contains("Approved"),
                "Loan was not approved: " + loanResult
        );

        // Step 4: Extract the generated loan account number
        String accountLabel = "Loan Account Number: ";

        Assert.assertTrue(
                loanResult.contains(accountLabel),
                "Loan account number was not present: " + loanResult
        );

        String loanAccountNumber = loanResult
                .substring(
                        loanResult.lastIndexOf(accountLabel)
                                + accountLabel.length()
                )
                .trim();

        Assert.assertFalse(
                loanAccountNumber.isEmpty(),
                "Loan account number should not be empty"
        );

        System.out.println(
                "Generated loan account number: " + loanAccountNumber
        );

        // Step 5: Open Accounts Overview
        By accountsOverviewLink =
                By.linkText("Accounts Overview");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        accountsOverviewLink
                )
        ).click();

        // Step 6: Wait for the AJAX-loaded account rows
        By accountLinks =
                By.cssSelector("#accountTable tbody tr td a");

        wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        accountLinks, 0
                )
        );

        // Step 7: Find the exact loan account row
        By loanRowLocator = By.xpath(
                "//table[@id='accountTable']//tbody/tr"
                        + "[td[1]/a[normalize-space()='"
                        + loanAccountNumber + "']]"
        );

        WebElement loanAccountRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loanRowLocator
                )
        );

        String loanRowText = loanAccountRow.getText();

        System.out.println("Loan account row: " + loanRowText);

        // Step 8: Verify the loan account balance
        Assert.assertTrue(
                loanRowText.contains("$200.00"),
                "Expected loan account balance of $200.00, "
                        + "but found: " + loanRowText
        );

        System.out.println(
                "PASS: Loan account " + loanAccountNumber
                        + " has the expected $200.00 balance."
        );
    }
}
