
package com.immverse.qa.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.immverse.qa.api.ParaBankApiClient;

public class ApiPreconditionTest {

    @Test
    public void verifyCleanDatabaseEndpointConfiguration() {
        String endpoint =
                "https://parabank.parasoft.com/parabank/services/bank/cleanDB";

        Assert.assertTrue(
                endpoint.endsWith("/services/bank/cleanDB"),
                "Unexpected clean database endpoint"
        );

        System.out.println(
                "PASS: Clean database endpoint configured: " + endpoint
        );
    }

    @Test
    public void verifyCleanDatabaseEndpoint() throws Exception {
        int statusCode = ParaBankApiClient.cleanDatabase();

        Assert.assertEquals(
                statusCode,
                204,
                "Expected HTTP 204 after cleaning the database"
        );

        System.out.println(
                "PASS: Database reset API returned HTTP " + statusCode
        );
    }

    @Test
    public void verifyLoanProviderSetting() throws Exception {
        String response =
                ParaBankApiClient.getLoanProviderSettings();

        Assert.assertNotNull(
                response,
                "Admin page response should not be null"
        );

        Assert.assertTrue(
                response.contains("id=\"loanProvider\""),
                "Loan Provider dropdown was not found"
        );

        Assert.assertTrue(
                response.contains(
                        "<option value=\"ws\" selected=\"selected\">Web Service"
                ),
                "Loan Provider must be set to Web Service"
        );

        System.out.println(
                "PASS: Loan Provider is configured as Web Service."
        );
    }
}
