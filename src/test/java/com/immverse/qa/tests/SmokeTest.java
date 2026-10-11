
package com.immverse.qa.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest extends BaseTest {

    @Test
    public void verifyParaBankHomePageLoads() {
        String actualUrl = driver.getCurrentUrl();
        String pageTitle = driver.getTitle();

        Assert.assertTrue(
                actualUrl.contains("parabank.parasoft.com"),
                "ParaBank URL was not loaded"
        );

        Assert.assertTrue(
                pageTitle.contains("ParaBank"),
                "ParaBank page title was incorrect"
        );

        System.out.println("Page URL: " + actualUrl);
        System.out.println("Page Title: " + pageTitle);
    }
}
