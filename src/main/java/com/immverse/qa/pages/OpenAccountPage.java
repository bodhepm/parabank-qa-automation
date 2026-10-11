
package com.immverse.qa.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OpenAccountPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By openNewAccountLink = By.linkText("Open New Account");
    private final By accountType = By.id("type");
    private final By openAccountButton =
            By.cssSelector("input[value='Open New Account']");
    private final By accountNumber =
            By.id("newAccountId");

    public OpenAccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openNewAccountPage() {
        wait.until(ExpectedConditions.elementToBeClickable(openNewAccountLink))
                .click();
    }

    public void selectCheckingAccount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountType));
        new Select(driver.findElement(accountType))
                .selectByVisibleText("CHECKING");
    }
    



public void selectSourceAccount() {
    By sourceAccount = By.id("fromAccountId");

    wait.until(driver -> {
        Select dropdown = new Select(driver.findElement(sourceAccount));
        return dropdown.getOptions().size() > 0;
    });

    Select dropdown = new Select(driver.findElement(sourceAccount));
    dropdown.selectByIndex(0);

    System.out.println("Selected source account: "
            + dropdown.getFirstSelectedOption().getText());
}







public void submitAccountRequest() {
    wait.until(ExpectedConditions.elementToBeClickable(openAccountButton)).click();

    // Wait for the account-opening result.
    wait.until(ExpectedConditions.or(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Account Opened!')]")),
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#rightPanel .error"))
    ));
}





public String getNewAccountNumber() {
    return wait.until(
        ExpectedConditions.visibilityOfElementLocated(accountNumber)
    ).getText();
}

}
