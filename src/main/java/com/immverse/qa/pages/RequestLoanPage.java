
package com.immverse.qa.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RequestLoanPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By requestLoanLink = By.linkText("Request Loan");
    private final By loanAmount = By.id("amount");
    private final By downPayment = By.id("downPayment");
    private final By fromAccount = By.id("fromAccountId");
    private final By applyButton = By.cssSelector("input[value='Apply Now']");
    private final By resultPanel = By.id("rightPanel");

    private final By loanResultTitle = By.xpath(
            "//*[@id='requestLoanResult']//h1"
    );
    private final By loanStatus = By.id("loanStatus");
    private final By loanAccountNumber = By.id("newAccountId");
    
    public RequestLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openLoanPage() {
        wait.until(ExpectedConditions.elementToBeClickable(requestLoanLink)).click();
    }


public void applyForLoan(String amount, String downPaymentAmount,
                         String sourceAccountNumber) {

    WebElement amountField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(loanAmount));
    amountField.clear();
    amountField.sendKeys(amount);

    WebElement downPaymentField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(downPayment));
    downPaymentField.clear();
    downPaymentField.sendKeys(downPaymentAmount);

    Select accountDropdown = new Select(
            wait.until(ExpectedConditions.visibilityOfElementLocated(fromAccount)));

    wait.until(d -> accountDropdown.getOptions().size() > 0);
    accountDropdown.selectByValue(sourceAccountNumber);

    System.out.println("Loan amount entered: " + amountField.getAttribute("value"));
    System.out.println("Down payment entered: " + downPaymentField.getAttribute("value"));
    System.out.println("Source account selected: "
            + accountDropdown.getFirstSelectedOption().getText());

    System.out.println("Loan form HTML:\n"
            + driver.findElement(By.id("rightPanel")).getAttribute("innerHTML"));
    wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();

    wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(loanResultTitle),
            ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#requestLoanError")
            )
    ));

    System.out.println("URL after Apply Now: " + driver.getCurrentUrl());
    System.out.println("Page content after response:\n"
            + driver.findElement(By.id("rightPanel")).getText());

}

public String getLoanResult() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(loanResultTitle));

    String title = driver.findElement(loanResultTitle).getText();
    String status = wait.until(
            ExpectedConditions.visibilityOfElementLocated(loanStatus)
    ).getText();

    String result = title + "\nStatus: " + status;

    if ("Approved".equalsIgnoreCase(status)) {
        String accountNumber = driver.findElement(loanAccountNumber).getText();
        result += "\nLoan Account Number: " + accountNumber;
    }

    return result;
}


}
