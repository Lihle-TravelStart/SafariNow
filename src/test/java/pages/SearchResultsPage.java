// In: pages/SearchResultsPage.java
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Represents the Search Results Page that appears after a search is performed.
 * Its primary responsibility is to provide methods for verifying the state
 * of the search results.
 */
public class SearchResultsPage {

    private final WebDriverWait wait;

    // --- Locators ---
    // This locator should target the main H1 header on the search results page.
    // Example: <h1 class="search-header">Accommodation in Cape Town</h1>
    @FindBy(tagName = "h1") // This is a common locator for the main page header
    private WebElement pageHeader;

    // --- Constructor ---
    public SearchResultsPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // --- Verification Methods ---

    /**
     * Waits for the page header to be visible and returns its text.
     * This is a key method for verifying that the correct search results page has loaded.
     *
     * @return The text content of the H1 page header.
     */
    public String getPageHeaderText() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
            return pageHeader.getText();
        } catch (Exception e) {
            System.err.println("Could not find the page header on the search results page.");
            return ""; // Return empty string on failure to prevent NullPointerException
        }
    }
}