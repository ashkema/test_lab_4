package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoWebShopOrderTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String userEmail = UserRegistrationHelper.getEmail();
    private final String userPassword = UserRegistrationHelper.getPassword();

    @BeforeEach
    public void setUp() {
        // Each test gets its own WebDriver session.
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
    public void testOrderUsingData1() throws IOException {
        performOrderTest("src/main/java/org/example/data1.txt");
    }


    @Test
    public void testOrderUsingData2() throws IOException {
        performOrderTest("src/main/java/org/example/data2.txt");
    }

    /**
     * Contains the common steps to:
     * - Log in using the registered user.
     * - Navigate, add products to the cart (reading from a text file), and complete checkout.
     */
    private void performOrderTest(String dataFileName) throws IOException {
        // 1. Open the website
        driver.get("https://demowebshop.tricentis.com/");

        // 2. Log in
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Log in"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys(userEmail);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password"))).sendKeys(userPassword);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Log in']"))).click();

        // 3. Navigate to Digital downloads
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Digital downloads"))).click();

        // 4. Add products from file
        List<String> products = Files.readAllLines(Paths.get(dataFileName));
        for (String productName : products) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(productName))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Add to cart']"))).click();
                driver.navigate().back();
            } catch (Exception e) {
                System.out.println("Could not process product: " + productName + " - " + e.getMessage());
            }
        }

        // 5. Open Shopping cart, agree and proceed to Checkout
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Shopping cart"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("termsofservice"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();

        // 6. Billing Address: select "New Address" and fill in the form data
// After clicking "Checkout", wait for the Billing step container to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkout-step-billing")));

// Billing Address: Select "New Address" from the billing address dropdown
        try {
            WebElement billingAddressSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("billing-address-select")));
            new Select(billingAddressSelect).selectByValue("");  // Empty value corresponds to "New Address"

// Wait until the new billing address form becomes visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("billing-new-address-form")));
        } catch (Exception e) { System.out.println("Could not process billing address selector: " + e.getMessage()); }


// Fill in the billing address form data
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_FirstName"))).clear();
        driver.findElement(By.id("BillingNewAddress_FirstName")).sendKeys("John");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_LastName"))).clear();
        driver.findElement(By.id("BillingNewAddress_LastName")).sendKeys("Doe");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_Email"))).clear();
        driver.findElement(By.id("BillingNewAddress_Email")).sendKeys("john.doe@example.com");

        WebElement countrySelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_CountryId")));
        new Select(countrySelect).selectByValue("1");  // "1" corresponds to United States

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_City"))).clear();
        driver.findElement(By.id("BillingNewAddress_City")).sendKeys("New York");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_Address1"))).clear();
        driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("123 Main St");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_ZipPostalCode"))).clear();
        driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("10001");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_PhoneNumber"))).clear();
        driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("1234567890");

// Click "Continue" in the Billing Address section
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@onclick='Billing.save()']")))
                .click();


        // 7. Shipping Address: For this test, assume existing address is acceptable; click Continue
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@onclick='Shipping.save()']")))
                .click();

        // 8. Shipping Method: click Continue
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@onclick='ShippingMethod.save()']")))
                .click();

        // 9. Payment Method: click Continue
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@onclick='PaymentMethod.save()']")))
                .click();

        // 10. Payment Information: click Continue
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='payment-info-buttons-container']//input[@onclick='PaymentInfo.save()']")))
                .click();

        // 11. Confirm Order: click "Confirm"
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='confirm-order-buttons-container']//input[@onclick='ConfirmOrder.save()']")))
                .click();

        // 12. Verify the confirmation message
        String confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".title"))).getText();
        assertTrue(confirmationMessage.contains("Your order has been successfully processed!"),
                "Order was not successfully processed.");
    }

}
