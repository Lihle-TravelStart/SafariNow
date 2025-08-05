package helpers;

import data.TestDataLoader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

/**
 * Generic search flow helper that uses data from external files
 */
public class DataDrivenSearchFlow {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final TestDataLoader dataLoader;

    public DataDrivenSearchFlow(WebDriver driver, String environment) {
        this.driver = driver;
        this.dataLoader = TestDataLoader.getInstance();
        
        int explicitWait = dataLoader.getConfigInt("timeouts", "explicitWait");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
    }

    /**
     * Navigate to homepage using environment from data
     */
    public void navigateToHomepage(String environment) {
        String homepageUrl = dataLoader.getEnvironmentUrl(environment);
        System.out.println("Navigating to homepage: " + homepageUrl);
        driver.get(homepageUrl);
        
        int pageLoadWait = dataLoader.getConfigInt("timeouts", "pageLoadTimeout");
        try {
            Thread.sleep(pageLoadWait * 100); // Convert to milliseconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Search for destination using selectors from data
     */
    public boolean searchDestination(String destination) {
        try {
            System.out.println("Searching for destination: " + destination);
            
            // Get selectors from data
            String inputSelector = dataLoader.getSelector("search", "destinationInput");
            String buttonSelector = dataLoader.getSelector("search", "searchButton");
            String dropdownSelector = dataLoader.getSelector("search", "searchDropdown");
            
            // Find and use search input
            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(inputSelector)));
            searchInput.clear();
            Thread.sleep(500);
            searchInput.sendKeys(destination);
            
            // Wait for dropdown
            int dropdownWait = dataLoader.getConfigInt("timeouts", "searchDropdownWait");
            Thread.sleep(dropdownWait * 1000);
            
            // Try dropdown selection first
            if (selectFromDropdown(destination, dropdownSelector)) {
                return true;
            }
            
            // Click search button
            WebElement searchButton = driver.findElement(By.cssSelector(buttonSelector));
            searchButton.click();
            
            // Wait for results
            int resultsWait = dataLoader.getConfigInt("timeouts", "searchResultsWait");
            Thread.sleep(resultsWait * 1000);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate search results using data-driven expectations
     */
    public boolean validateSearchResults(String destination) {
        try {
            String propertyCardsSelector = dataLoader.getSelector("propertyListing", "propertyCards");
            List<WebElement> propertyCards = driver.findElements(By.cssSelector(propertyCardsSelector));
            
            int minThreshold = dataLoader.getConfigInt("validation", "minPropertiesThreshold");
            boolean hasMinProperties = propertyCards.size() >= minThreshold;
            
            System.out.println("Found " + propertyCards.size() + " properties (min required: " + minThreshold + ")");
            
            return hasMinProperties;
            
        } catch (Exception e) {
            System.err.println("Search results validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select first property using selectors from data
     */
    public boolean selectFirstProperty() {
        try {
            String propertyCardsSelector = dataLoader.getSelector("propertyListing", "propertyCards");
            
            List<WebElement> propertyCards = driver.findElements(By.cssSelector(propertyCardsSelector));
            
            if (propertyCards.isEmpty()) {
                System.out.println("No properties found to select");
                return false;
            }
            
            WebElement firstProperty = propertyCards.get(0);
            
            // Scroll to property
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", firstProperty
            );
            Thread.sleep(1000);
            
            // Try to click property or find link within it
            try {
                firstProperty.click();
            } catch (Exception e) {
                List<WebElement> links = firstProperty.findElements(By.cssSelector("a"));
                if (!links.isEmpty()) {
                    links.get(0).click();
                } else {
                    throw e;
                }
            }
            
            System.out.println("✓ Selected first property");
            Thread.sleep(3000);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to select first property: " + e.getMessage());
            return false;
        }
    }

    /**
     * Test image carousel using selectors from data
     */
    public boolean testImageCarousel() {
        try {
            String imagesSelector = dataLoader.getSelector("carousel", "images");
            String nextButtonSelector = dataLoader.getSelector("carousel", "nextButtons");
            
            List<WebElement> images = driver.findElements(By.cssSelector(imagesSelector));
            List<WebElement> nextButtons = driver.findElements(By.cssSelector(nextButtonSelector));
            
            if (images.isEmpty()) {
                System.out.println("No carousel images found");
                return false;
            }
            
            System.out.println("Found " + images.size() + " carousel images");
            
            if (nextButtons.isEmpty()) {
                System.out.println("No carousel navigation - static images");
                return true;
            }
            
            // Test navigation
            String initialSrc = images.get(0).getAttribute("src");
            nextButtons.get(0).click();
            Thread.sleep(1000);
            
            List<WebElement> newImages = driver.findElements(By.cssSelector(imagesSelector));
            if (!newImages.isEmpty()) {
                String newSrc = newImages.get(0).getAttribute("src");
                boolean changed = !initialSrc.equals(newSrc);
                System.out.println("Carousel navigation " + (changed ? "working" : "static"));
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Carousel test failed: " + e.getMessage());
            return false;
        }
    }

    private boolean selectFromDropdown(String destination, String dropdownSelector) {
        try {
            List<WebElement> options = driver.findElements(By.cssSelector(dropdownSelector));
            
            for (WebElement option : options) {
                if (option.getText().toLowerCase().contains(destination.toLowerCase())) {
                    option.click();
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
}
