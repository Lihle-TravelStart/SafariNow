package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ImageHelper {

    private WebDriver driver;
    private WebDriverWait wait;

    public ImageHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // Adjust timeout as needed
    }

    /**
     * Checks if an image is displayed correctly (not broken).
     *
     * @param imageLocator The By locator for the image.
     * @return True if the image exists, is visible, and is loaded correctly; false otherwise.
     */
    public boolean isImageDisplayed(By imageLocator) {
        try {
            WebElement imageElement = wait.until(ExpectedConditions.presenceOfElementLocated(imageLocator));
            if (!imageElement.isDisplayed()) {
                System.err.println("Image is not visible.");
                return false;
            }

            // Check if the image is loaded correctly using JavaScript
            Boolean isImageLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0",
                    imageElement);

            if (isImageLoaded) {
                System.out.println("Image is displayed and loaded correctly.");
                return true;
            } else {
                System.err.println("Image is broken or not loaded.");
                return false;
            }
        } catch (Exception e) {
            System.err.println(STR."Error checking image: \{e.getMessage()}");
            return false;
        }
    }
}