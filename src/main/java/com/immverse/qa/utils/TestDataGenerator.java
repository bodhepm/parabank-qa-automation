
package com.immverse.qa.utils;

import java.util.UUID;

public class TestDataGenerator {

    private TestDataGenerator() {
        // Utility class
    }

    public static String generateUsername() {
        return "qa_auto_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }
}