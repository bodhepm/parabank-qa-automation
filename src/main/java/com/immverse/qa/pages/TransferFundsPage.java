
package com.immverse.qa.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TransferFundsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Confirmed ParaBank locators
    private final By amountField = By.id("amount");
    private final By fromAccountDropdown = By.id("fromAccountId");
    private final By toAccountDropdown = By.id("toAccountId");

    private final By transferButton =
            By.cssSelector("#transferForm input[type='submit']");

    private final By transferResult = By.id("showResult");
    private final By transferError = By.id("showError");

    public TransferFundsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(20));
    }

    public void openTransferFundsPage() {
        By transferLink = By.linkText("Transfer Funds");

        wait.until(
                ExpectedConditions.elementToBeClickable(transferLink)
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(amountField)
        );
    }

    public void transferFunds(
            String amount,
            String fromAccount,
            String toAccount) {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(amountField)
        ).clear();

        driver.findElement(amountField).sendKeys(amount);

        selectAccount(fromAccountDropdown, fromAccount);
        selectAccount(toAccountDropdown, toAccount);

        wait.until(
                ExpectedConditions.elementToBeClickable(transferButton)
        ).click();

        wait.until(d ->
                d.findElement(transferResult).isDisplayed()
                || d.findElement(transferError).isDisplayed()
        );
    }

    private void selectAccount(
            By dropdownLocator, String accountNumber) {

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(dropdownLocator)
        );

        wait.until(d ->
                new Select(d.findElement(dropdownLocator))
                        .getOptions()
                        .stream()
                        .anyMatch(option ->
                                option.getText().trim()
                                        .equals(accountNumber))
        );

        new Select(dropdown).selectByVisibleText(accountNumber);
    }

    public String getTransferResult() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(transferResult)
        ).getText();
    }

    public String getTransferError() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(transferError)
        ).getText();
    }
}
