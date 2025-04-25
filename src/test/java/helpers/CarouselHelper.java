package helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CarouselHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ImageHelper imageHelper;

    // Constructor
    public CarouselHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.imageHelper = new ImageHelper(driver);
    }

    /**
     * Checks if a carousel exists on the page.
     *
     * @param carouselLocator      The By locator for the carousel container.
     * @param carouselItemsLocator The By locator for the carousel items.
     * @return True if the carousel exists, false otherwise.
     */
    public boolean isCarouselPresent(By carouselLocator, By carouselItemsLocator) {

        try {
            WebElement carouselContainer = wait.until(ExpectedConditions.presenceOfElementLocated(carouselLocator));
            scrollToElement(carouselContainer);
            // Add a wait after scrolling.
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.err.println("The thread was interrupted.");
            }
            // Wait for *at least one* item to be visible.
            wait.until(ExpectedConditions.visibilityOfElementLocated(carouselItemsLocator));

            // Check if we found at least one item
            List<WebElement> carouselItems = driver.findElements(carouselItemsLocator);
            if (carouselItems.isEmpty()) {
                return false;
            }
            // Check if the first image is displayed correctly
            return imageHelper.isImageDisplayed(carouselItemsLocator);
        } catch (NoSuchElementException e) {
            System.err.println(STR."Carousel not found: \{e.getMessage()}");
            return false;
        } catch (Exception e) {
            System.err.println(STR."An error occurred while checking for carousel: \{e.getMessage()}");
            return false;
        }
    }

    /**
     * Clicks the next button in a carousel.
     *
     * @param nextButtonLocator The By locator for the "next" button.
     */
    public void clickNext(By nextButtonLocator, By imageLocator) {
        try {
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(nextButtonLocator));
            nextButton.click();
            waitForImageLoad(imageLocator);
        } catch (Exception e) {
            System.err.println("Error clicking 'Next' button: " + e.getMessage());
        }
    }

    /**
     * Clicks the previous button in a carousel.
     *
     * @param previousButtonLocator The By locator for the "previous" button.
     */
    public void clickPrevious(By previousButtonLocator) {
        try {
            WebElement previousButton = wait.until(ExpectedConditions.elementToBeClickable(previousButtonLocator));
            previousButton.click();
            waitForCarouselTransition();
        } catch (Exception e) {
            System.err.println("Error clicking 'Previous' button: " + e.getMessage());
        }
    }

    /**
     * Gets the list of images in the carousel.
     *
     * @param imageLocator The By locator for the carousel images.
     * @return A list of WebElement representing the images.
     */
    public List<WebElement> getCarouselImages(By imageLocator) {
        try {
            return driver.findElements(imageLocator);
        } catch (Exception e) {
            System.err.println("Error getting carousel images: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the first image in the list of images
     *
     * @param imageLocator the locator for the carousel images
     * @return the title attribute of the first image
     */
    public String getFirstImageTitle(By imageLocator) {
        By firstImageLocator = By.cssSelector(".swiper-slide-active img.swiper-lazy-loaded");
        try {
            WebElement firstImage = wait.until(ExpectedConditions.presenceOfElementLocated(firstImageLocator));
            return firstImage.getAttribute("title");
        } catch (Exception e) {
            System.err.println("Error getting the first image title: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if images are changing in the carousel by clicking next.
     *
     * @param nextButtonLocator The By locator for the "next" button.
     * @param imageLocator      The By locator for the carousel images.
     * @return true if the image changes false if otherwise
     */
    public boolean areImagesChanging(By nextButtonLocator, By imageLocator) {
        WebElement firstImage;
        String initialImageSrc;
        String updatedImageSrc;
        By firstImageLocator = By.cssSelector(".swiper-slide-active img.swiper-lazy-loaded");

        try {
            // Wait for the first image to be present and visible
            firstImage = wait.until(ExpectedConditions.presenceOfElementLocated(firstImageLocator));

            initialImageSrc = firstImage.getAttribute("src");
            if (initialImageSrc == null || initialImageSrc.isEmpty()) {
                throw new AssertionError("Image source is null or empty before clicking next.");
            }

            clickNext(nextButtonLocator, imageLocator);

            wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(firstImageLocator, "src", initialImageSrc)));
            firstImage = wait.until(ExpectedConditions.presenceOfElementLocated(firstImageLocator));
            updatedImageSrc = firstImage.getAttribute("src");

            Assert.assertNotEquals(updatedImageSrc, initialImageSrc, "The image did not change when clicking next.");
            return true;

        } catch (Exception e) {
            System.err.println(STR."Error in areImagesChanging method: \{e.getMessage()}");
            System.err.println(STR."Current URL: \{driver.getCurrentUrl()}");
            System.err.println(STR."Page Source: \{driver.getPageSource()}");
            return false;
        }
    }

    /**
     * Checks if images are changing in the carousel by clicking previous.
     *
     * @param prevButtonLocator The By locator for the "previous" button.
     * @param imageLocator      The By locator for the carousel images.
     * @return true if the image changes false if otherwise
     */
    public boolean areImagesChangingBackwards(By prevButtonLocator, By imageLocator) {
        WebElement firstImage;
        String initialImageSrc;
        String updatedImageSrc;
        By firstImageLocator = By.cssSelector(".swiper-slide-active img.swiper-lazy-loaded");
        try {
            firstImage = wait.until(ExpectedConditions.presenceOfElementLocated(firstImageLocator));
            initialImageSrc = firstImage.getAttribute("src");

            if (initialImageSrc == null) {
                throw new AssertionError("Image source is null before clicking previous.");
            }

            clickPrevious(prevButtonLocator);
            wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(firstImageLocator, "src", initialImageSrc)));
            firstImage = wait.until(ExpectedConditions.presenceOfElementLocated(firstImageLocator));

            updatedImageSrc = firstImage.getAttribute("src");

            Assert.assertNotEquals(updatedImageSrc, initialImageSrc, "The image did not change when clicking previous.");
            return true;

        } catch (Exception e) {
            System.err.println("The image did not change: " + e.getMessage());
            return false;
        }
    }

    /**
     * Moves through the carousel in the specified direction.
     *
     * @param buttonLocator The locator for the "next" or "previous" button.
     * @param imageLocator  The locator for the carousel images.
     * @param direction     The direction to move ("next" or "previous").
     * @return true if the carousel moved through multiple images, false otherwise.
     */
    public boolean moveThroughCarousel(By buttonLocator, By imageLocator, String direction) {
        Set<String> seenImageTitles = new HashSet<>();
        String initialFirstImageTitle = getFirstImageTitle(imageLocator);
        int maxClicks = 20;
        int numberOfClicks = 0;

        for (int i = 0; i < maxClicks; i++) {
            String currentFirstImageTitle = getFirstImageTitle(imageLocator);
            if (currentFirstImageTitle == null) {
                System.err.println("Error no images were found");
                Assert.fail("Error no images were found");
            }
            if (numberOfClicks > 2 && seenImageTitles.contains(currentFirstImageTitle) && currentFirstImageTitle.equals(initialFirstImageTitle)) {
                break;
            }

            seenImageTitles.add(currentFirstImageTitle);
            numberOfClicks++;

            if (direction.equalsIgnoreCase("next")) {
                clickNext(buttonLocator, imageLocator);
            } else if (direction.equalsIgnoreCase("previous")) {
                clickPrevious(buttonLocator);
            }
            checkIfAllImagesInCarouselAreDisplayed(imageLocator);
        }

        boolean hasMoved = seenImageTitles.size() > 1;
        if (!hasMoved) {
            System.err.println("Carousel did not moved through its elements " + direction);
        }

        return hasMoved;
    }

    /**
     * Waits for a specific image in the carousel to be loaded.
     *
     * @param imageLocator The locator for the carousel images.
     */
    private void waitForImageLoad(By imageLocator) {
        By firstImageLocator = By.cssSelector(".swiper-slide-active img.swiper-lazy-loaded");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstImageLocator));
        } catch (Exception e) {
            System.err.println(STR."Error waiting for the image to load: \{e.getMessage()}");
        }
    }

    private void waitForCarouselTransition() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println(STR."Wait interrupted: \{e.getMessage()}");
        }
    }

    /**
     * Checks if all images in the carousel are displayed.
     *
     * @param imageLocator The locator for the carousel images.
     */
    public void checkIfAllImagesInCarouselAreDisplayed(By imageLocator) {
        By activeImageLocator = By.cssSelector(".swiper-slide-active img.swiper-lazy-loaded");
        WebElement image = wait.until(ExpectedConditions.presenceOfElementLocated(activeImageLocator));
        if (!imageHelper.isImageDisplayed(activeImageLocator)) {
            System.err.println("At least one image in the carousel is not displayed correctly.");
            Assert.fail("At least one image in the carousel is not displayed correctly.");
            return;

        }
    }

    /**
     * Scrolls a web element into view.
     *
     * @param element The web element to scroll into view.
     */
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", 150);
    }
}