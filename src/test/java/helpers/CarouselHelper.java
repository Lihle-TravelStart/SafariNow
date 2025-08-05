package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.SafariNowHomePage;

import java.time.Duration;
import java.util.List;

public class CarouselHelper {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SafariNowHomePage homePage;

    public CarouselHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.homePage = new SafariNowHomePage(driver);
    }

    /**
     * Enhanced method to check if carousel is present using multiple strategies.
     */
    public boolean isCarouselPresent(By container, By items) {
        try {
            System.out.println("Checking carousel presence...");

            // First try the provided locators
            List<WebElement> containerElements = driver.findElements(container);
            List<WebElement> itemElements = driver.findElements(items);

            System.out.println(STR."Container elements found: \{containerElements.size()}");
            System.out.println(STR."Item elements found: \{itemElements.size()}");

            // Check if elements are displayed
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

    /**
     * Enhanced method with better error handling and alternative strategies.
     */
    public boolean areImagesChanging(By nextButton, By items) {
        try {
            System.out.println("Testing if images change when clicking next...");

            // Get initial state
            List<WebElement> initialItems = driver.findElements(items);
            if (initialItems.isEmpty()) {
                System.err.println("No carousel items found");
                return false;
            }

            String initialSrc = initialItems.get(0).getAttribute("src");
            System.out.println(STR."Initial image src: \{initialSrc}");

            // Try to click next button
            List<WebElement> nextButtons = driver.findElements(nextButton);
            if (nextButtons.isEmpty()) {
                System.err.println("No next button found");
                return false;
            }

            WebElement nextBtn = nextButtons.get(0);
            if (!nextBtn.isDisplayed() || !nextBtn.isEnabled()) {
                System.err.println("Next button is not clickable");
                return false;
            }

            nextBtn.click();
            Thread.sleep(1000); // Wait for animation

            // Check if image changed
            List<WebElement> newItems = driver.findElements(items);
            if (newItems.isEmpty()) {
                System.err.println("No carousel items found after click");
                return false;
            }

            String newSrc = newItems.get(0).getAttribute("src");
            System.out.println(STR."New image src: \{newSrc}");

            boolean changed = !initialSrc.equals(newSrc);
            System.out.println(STR."Images changed: \{changed}");

            return changed;

        } catch (Exception e) {
            System.err.println(STR."Error testing image changes: \{e.getMessage()}");
            return false;
        }
    }

    public boolean areImagesChangingBackwards(By prevButton, By items) {
        try {
            System.out.println("Testing if images change when clicking previous...");

            List<WebElement> initialItems = driver.findElements(items);
            if (initialItems.isEmpty()) {
                System.err.println("No carousel items found");
                return false;
            }

            String initialSrc = initialItems.get(0).getAttribute("src");

            List<WebElement> prevButtons = driver.findElements(prevButton);
            if (prevButtons.isEmpty()) {
                System.err.println("No previous button found");
                return false;
            }

            WebElement prevBtn = prevButtons.get(0);
            if (!prevBtn.isDisplayed() || !prevBtn.isEnabled()) {
                System.err.println("Previous button is not clickable");
                return false;
            }

            prevBtn.click();
            Thread.sleep(1000);

            List<WebElement> newItems = driver.findElements(items);
            if (newItems.isEmpty()) {
                return false;
            }

            String newSrc = newItems.get(0).getAttribute("src");
            boolean changed = !initialSrc.equals(newSrc);
            System.out.println(STR."Images changed backwards: \{changed}");

            return changed;

        } catch (Exception e) {
            System.err.println(STR."Error testing backward image changes: \{e.getMessage()}");
            return false;
        }
    }

    public boolean moveThroughCarousel(By button, By items, String direction) {
        try {
            System.out.println(STR."Testing carousel movement in \{direction} direction...");

            List<WebElement> itemElements = driver.findElements(items);
            if (itemElements.isEmpty()) {
                System.err.println("No carousel items found");
                return false;
            }

            int totalItems = itemElements.size();
            System.out.println(STR."Total carousel items: \{totalItems}");

            if (totalItems <= 1) {
                System.out.println("Only one item in carousel, movement test not applicable");
                return true;
            }

            List<WebElement> buttons = driver.findElements(button);
            if (buttons.isEmpty()) {
                System.err.println(STR."No \{direction} button found");
                return false;
            }

            WebElement btn = buttons.get(0);

            // Try to move through several items (not necessarily all)
            int maxClicks = Math.min(totalItems, 5); // Test up to 5 clicks
            int successfulClicks = 0;

            for (int i = 0; i < maxClicks; i++) {
                try {
                    if (btn.isDisplayed() && btn.isEnabled()) {
                        String beforeSrc = driver.findElements(items).get(0).getAttribute("src");
                        btn.click();
                        Thread.sleep(1000);
                        String afterSrc = driver.findElements(items).get(0).getAttribute("src");

                        if (!beforeSrc.equals(afterSrc)) {
                            successfulClicks++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println(STR."Error on click \{i + 1}: \{e.getMessage()}");
                    break;
                }
            }

            System.out.println(STR."Successful \{direction} clicks: \{successfulClicks} out of \{maxClicks}");
            return successfulClicks > 0;

        } catch (Exception e) {
            System.err.println(STR."Error moving through carousel: \{e.getMessage()}");
            return false;
        }
    }
}
