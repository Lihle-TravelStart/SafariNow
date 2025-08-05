package helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CarouselHelper {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CarouselHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isCarouselPresent(By container, By items) {
        try {
            System.out.println("Checking carousel presence...");
            List<WebElement> containerElements = driver.findElements(container);
            List<WebElement> itemElements = driver.findElements(items);

            System.out.println(STR."Container elements found: \{containerElements.size()}");
            System.out.println(STR."Item elements found: \{itemElements.size()}");

            boolean containerDisplayed = containerElements.stream().anyMatch(WebElement::isDisplayed);
            boolean itemsDisplayed = itemElements.stream().anyMatch(WebElement::isDisplayed);

            System.out.println(STR."Container displayed: \{containerDisplayed}");
            System.out.println(STR."Items displayed: \{itemsDisplayed}");

            return containerDisplayed && itemsDisplayed && !itemElements.isEmpty();
        } catch (Exception e) {
            System.err.println(STR."Error checking carousel presence: \{e.getMessage()}");
            return false;
        }
    }

    public boolean areImagesChanging(By nextButton, By items) {
        String initialSrc = null;
        try {
            System.out.println("Testing if images change when clicking next...");
            initialSrc = getActiveImageSrc(items);
            if (initialSrc == null) {
                System.err.println("✗ Could not determine the initial active image source. Aborting 'next' button test.");
                return false;
            }
            System.out.println(STR."Initial active image src: \{initialSrc}");

            final String finalInitialSrc = initialSrc;

            driver.findElement(nextButton).click();

            wait.until(driver -> {
                String currentSrc = getActiveImageSrc(items);
                return currentSrc != null && !currentSrc.equals(finalInitialSrc);
            });

            // IMPROVEMENT: Add a brief pause to allow all animations to settle.
            Thread.sleep(500);

            System.out.println(STR."✓ Images changed successfully. New active src: \{getActiveImageSrc(items)}");
            return true;
        } catch (TimeoutException e) {
            System.err.println("✗ TIMEOUT: The active image did not change after clicking the next button.");
            return false;
        } catch (Exception e) {
            System.err.println(STR."✗ Error testing image changes: \{e.getMessage()}");
            return false;
        }
    }

    public boolean areImagesChangingBackwards(By prevButton, By items) {
        String initialSrc = null;
        try {
            System.out.println("Testing if images change when clicking previous...");
            initialSrc = getActiveImageSrc(items);
            if (initialSrc == null) {
                System.err.println("✗ Could not determine the initial active image source for backward check. Aborting 'previous' button test.");
                return false;
            }
            System.out.println(STR."Initial active image src for 'previous' check: \{initialSrc}");

            final String finalInitialSrc = initialSrc;

            WebElement prevBtn = driver.findElement(prevButton);
            if (!prevBtn.isDisplayed() || !prevBtn.isEnabled()) {
                System.err.println("✗ Previous button is not displayed or not enabled. Cannot test backward change.");
                return false;
            }

            // REVERTED: Use a standard Selenium click, which is proven to work for the 'next' button.
            prevBtn.click();

            wait.until(driver -> {
                String currentSrc = getActiveImageSrc(items);
                return currentSrc != null && !currentSrc.equals(finalInitialSrc);
            });

            // IMPROVEMENT: Add a brief pause here as well for consistency.
            Thread.sleep(500);

            System.out.println(STR."✓ Images changed backwards successfully. New active src: \{getActiveImageSrc(items)}");
            return true;
        } catch (TimeoutException e) {
            String lastSeenSrc = getActiveImageSrc(items);
            System.err.println("✗ TIMEOUT: The active image did not change after clicking the previous button.");
            System.err.println(STR."  - Initial Src was: \{initialSrc}");
            System.err.println(STR."  - Last Seen Src is: \{lastSeenSrc}");
            return false;
        } catch (Exception e) {
            System.err.println(STR."✗ Error testing backward image changes: \{e.getMessage()}");
            return false;
        }
    }

    /**
     * REFACTORED: This method is now more robust and communicative. It uses an
     * internal wait to handle timing issues and provides detailed logging on failure.
     */
    private String getActiveImageSrc(By itemsLocator) {
        // Use a short-lived wait specifically for this operation to handle timing issues.
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            // Build a more direct locator for the image inside the active slide.
            String baseItemsXpath = itemsLocator.toString().replace("By.xpath: ", "");
            By activeImageLocator = By.xpath(baseItemsXpath + "[contains(@class, 'swiper-slide-active')]/descendant::img");

            // Wait for the active image to be present in the DOM.
            WebElement activeImage = shortWait.until(ExpectedConditions.presenceOfElementLocated(activeImageLocator));

            // Now that we have the image, check if it's properly loaded.
            if (isImageLoaded(activeImage)) {
                return activeImage.getAttribute("src");
            } else {
                System.err.println("✗ Found the active image element, but it is not fully loaded.");
                return null;
            }
        } catch (TimeoutException e) {
            // This is the most likely failure point if the active class isn't applied in time.
            System.err.println("✗ TIMEOUT in getActiveImageSrc: Could not find an element matching the active slide image locator within 5 seconds.");
            // For deeper debugging, let's see if we can find ANY items.
            List<WebElement> allItems = driver.findElements(itemsLocator);
            System.err.println(STR."  - Debug: Found \{allItems.size()} total carousel items with the base locator.");
            return null;
        } catch (Exception e) {
            // Catch any other unexpected errors.
            System.err.println(STR."✗ An unexpected error occurred in getActiveImageSrc: \{e.getMessage()}");
            return null;
        }
    }

    /**
     * This private helper uses JavaScript to provide a reliable check for a single image.
     */
    private boolean isImageLoaded(WebElement imageElement) {
        try {
            // Ensure the element is not null before executing script
            if (imageElement == null) return false;
            return (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0",
                    imageElement);
        } catch (Exception e) {
            return false; // If any error occurs, assume the image is not loaded.
        }
    }
}