package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class SeleniumUtils {

    private static WebDriver driver;
    private static Properties properties = new Properties();
    private static final int DEFAULT_TIMEOUT = 10;
    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
    }

    // --- WAIT METHODS ---
    public static void waitForVisibility(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    // --- ELEMENT INTERACTION METHODS ---
    public static void clickElement(By locator) {
        waitForElementToBeClickable(locator, DEFAULT_TIMEOUT);
        driver.findElement(locator).click();
    }

    public static void enterText(By locator, String text) {
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    // --- AUTO-SUGGESTION SELECTION METHOD ---
    public static void selectAutoSuggestion(By inputLocator, String inputText, By suggestionLocator) {
        WebElement inputField = driver.findElement(inputLocator);
        inputField.clear();
        inputField.sendKeys(inputText);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));

        List<WebElement> suggestions = driver.findElements(suggestionLocator);
        if (!suggestions.isEmpty()) {
            suggestions.get(0).click();
        } else {
            System.out.println("No suggestions found for the input: " + inputText);
        }
    }

    // --- ASSERTION METHODS ---
    public static void assertElementVisible(By locator) {
        Assert.assertTrue(driver.findElement(locator).isDisplayed(), "Element not visible: " + locator);
    }

    public static void assertPageTitle(String expectedTitle) {
        Assert.assertEquals(driver.getTitle(), expectedTitle, "Page title does not match.");
    }

    // --- CONFIGURATION LOADER ---
//    static {
//        try {
//            FileInputStream fileInputStream = new FileInputStream("config.properties");
//            properties.load(fileInputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public boolean isImageLoaded(WebElement  imageLocator, int timeoutSeconds) {
        WebElement image = driver.findElement((By) imageLocator);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // Execute JS to check if the image is loaded
        Boolean isLoaded = (Boolean) jsExecutor.executeScript(
                "return arguments[0].complete && arguments[0].naturalWidth > 0", image);

        return isLoaded != null && isLoaded;
    }
    public void checkNextImageLoaded(By nextButtonLocator, By imageLocator) {
        // Click the Next button
        WebElement nextButton = driver.findElement(nextButtonLocator);
        nextButton.click();

        // Wait for the next image to load
        isImageLoaded((WebElement) imageLocator, 10);
        System.out.println("Next image is loaded.");
    }

    public static String getConfigProperty(String key) {
        return properties.getProperty(key);
    }
}
