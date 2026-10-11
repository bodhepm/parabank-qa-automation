
package com.immverse.qa.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.immverse.qa.pages.RegisterPage;
import com.immverse.qa.utils.TestDataGenerator;

public class RegistrationTest extends BaseTest {

    @Test
    public void verifyUserCanRegisterSuccessfully() {

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

        String message = registerPage.getRegistrationMessage();

        Assert.assertTrue(
                message.contains("Welcome"),
                "Registration did not succeed. Actual message: " + message
        );

        System.out.println("Registered username: " + username);
        System.out.println("Registration result: " + message);
    }
}