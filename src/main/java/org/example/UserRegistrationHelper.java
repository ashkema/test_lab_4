package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserRegistrationHelper {
    private static String email;
    private static final String password = "Password123!";

    /**
     * Lazy registration of a new user in a separate WebDriver session.
     * This method will be called once before any tests that need credentials.
     */
    public static synchronized void registerUserIfNeeded() {
        if (email != null) {
            return; // User already registered.
        }
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Open the website and start registration
            driver.get("https://demowebshop.tricentis.com/");
            driver.findElement(By.linkText("Log in")).click();
            driver.findElement(By.xpath("//input[@value='Register']")).click();

            // Fill in registration form
            driver.findElement(By.id("gender-male")).click();
            driver.findElement(By.id("FirstName")).sendKeys("John");
            driver.findElement(By.id("LastName")).sendKeys("Doe");
            email = "johndoe" + System.currentTimeMillis() + "@example.com";
            driver.findElement(By.id("Email")).sendKeys(email);
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("ConfirmPassword")).sendKeys(password);

            // Click "Register"
            driver.findElement(By.id("register-button")).click();

            // Optionally, verify registration success then click "Continue"
            String registrationResult = driver.findElement(By.className("result")).getText();
            if (!registrationResult.contains("Your registration completed")) {
                throw new RuntimeException("Registration failed: " + registrationResult);
            }
            driver.findElement(By.xpath("//input[@value='Continue']")).click();

        } finally {
            // Ensure that the registration session is closed.
            driver.quit();
        }
    }

    public static String getEmail() {
        registerUserIfNeeded();
        return email;
    }

    public static String getPassword() {
        registerUserIfNeeded();
        return password;
    }
}
