
package com.immverse.qa.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.nio.charset.StandardCharsets;

public class ParaBankApiClient {

    private static final String BASE_URL ="https://parabank.parasoft.com/parabank";

    public static int cleanDatabase() throws Exception {
        HttpURLConnection connection = (HttpURLConnection)
                
        java.net.URI.create(BASE_URL + "/services/bank/cleanDB").toURL().openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);

        int statusCode = connection.getResponseCode();
        connection.disconnect();

        return statusCode;
    }

    public static String getLoanProviderSettings() throws Exception {
        HttpURLConnection connection = (HttpURLConnection)
        		java.net.URI.create(BASE_URL + "/admin.htm").toURL()
                        .openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);

        int statusCode = connection.getResponseCode();

        InputStream stream = statusCode < 400
                ? connection.getInputStream()
                : connection.getErrorStream();

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        stream, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } finally {
            connection.disconnect();
        }

        return response.toString();
    }
}
