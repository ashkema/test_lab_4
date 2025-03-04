package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DemoWebShopRegistrationTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRegistration() {
        // Call the helper to register a new user in a separate session.
        UserRegistrationHelper.registerUserIfNeeded();
        // Assert that the registration helper now has valid credentials.
        String registeredEmail = UserRegistrationHelper.getEmail();
        assertNotNull(registeredEmail, "The user email should have been registered successfully.");
        System.out.println("Registered user: " + registeredEmail);
    }
}
