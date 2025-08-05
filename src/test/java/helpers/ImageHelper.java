package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Provides robust, reusable utility methods for verifying image elements.
 * It is designed to be flexible by handling both WebElements and By locators.
 */
public class ImageHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ImageHelper(WebDriver driver) {
        this.driver = driver;
        // A shorter, more reasonable default wait for an image to be present.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * The primary, most efficient method. Checks if a given image WebElement has loaded correctly.
     * This is ideal when you have already located the element.
     *
     * @param imageElement The <img/> WebElement to check.
     * @return true if the image is visible and has a naturalWidth > 0, false otherwise.
     */
    public boolean isImageLoaded(WebElement imageElement) {
        try {
            // First, ensure the element is actually visible on the page.
            wait.until(ExpectedConditions.visibilityOf(imageElement));

            // Then, execute the script to check if it's a broken image.
            Boolean imageLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0",
                    imageElement);

            return imageLoaded != null && imageLoaded;
        } catch (Exception e) {
            System.err.println(STR."Error checking image status for element [\{imageElement}]: \{e.getMessage()}");
            return false;
        }
    }

    /**
     * A convenience method that first finds an image by its locator and then checks if it has loaded.
     * This maintains compatibility with tests that pass locators.
     *
     * @param imageLocator The By locator for the image.
     * @return true if the image is found, visible, and loaded correctly; false otherwise.
     */
    public boolean isImageLoaded(By imageLocator) {
        try {
            // Find the element using the locator, then delegate to the primary method.
            WebElement imageElement = wait.until(ExpectedConditions.presenceOfElementLocated(imageLocator));
            return isImageLoaded(imageElement);
        } catch (Exception e) {
            System.err.println(STR."Error finding or checking image with locator [\{imageLocator}]: \{e.getMessage()}");
            return false;
        }
    }
}