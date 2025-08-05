package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Represents the Accommodation List Page (search results page).
 * This class encapsulates the elements and actions available on this page,
 * such as interacting with date filters or selecting a property.
 */
public class AccomodationListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- Locators (using @FindBy for consistency and encapsulation) ---

    @FindBy(id = "Checkin") // Using ID is more stable than XPath if available
    private WebElement checkInDateInput;

    @FindBy(id = "Checkout")
    private WebElement checkOutDateInput;

    // This is a more practical locator for this page: the list of property cards.
    // IMPORTANT: The selector ".establishment-card-container" is an example.
    // You must replace it with the actual, stable CSS selector for your property cards.
    @FindBy(css = ".establishment-card-container")
    private List<WebElement> propertyCards;

    // --- Constructor ---

    public AccomodationListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // This initializes all elements annotated with @FindBy
        PageFactory.initElements(driver, this);
    }

    // --- Action Methods ---

    /**
     * Enters a date into the check-in date field after ensuring it is visible.
     * @param checkInDate The date string to enter (e.g., "2025-01-20").
     */
    public void enterCheckInDate(String checkInDate) {
        wait.until(ExpectedConditions.visibilityOf(checkInDateInput));
        checkInDateInput.clear();
        checkInDateInput.sendKeys(checkInDate);
    }

    /**
     * Enters a date into the check-out date field after ensuring it is visible.
     * @param checkOutDate The date string to enter (e.g., "2025-01-25").
     */
    public void enterCheckOutDate(String checkOutDate) {
        wait.until(ExpectedConditions.visibilityOf(checkOutDateInput));
        checkOutDateInput.clear();
        checkOutDateInput.sendKeys(checkOutDate);
    }

    /**
     * Clicks on the first property in the search results list to navigate to its details page.
     * This is a crucial method for creating end-to-end test flows.
     * Throws a RuntimeException if no properties are found or the first one is not clickable.
     */
    public void selectFirstProperty() {
        try {
            // Wait until at least one property card is visible and clickable
            wait.until(d -> !propertyCards.isEmpty() && propertyCards.get(0).isDisplayed());
            wait.until(ExpectedConditions.elementToBeClickable(propertyCards.get(0))).click();
            System.out.println("Clicked on the first property in the accommodation list.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to select the first property from the accommodation list.", e);
        }
    }

    // --- Verification Methods ---

    /**
     * Verifies if the current page title matches the expected title.
     * This method is now useful for assertions in a test.
     * @param expectedTitle The title you expect the page to have.
     * @return true if the title matches (case-insensitive), false otherwise.
     */
    public boolean isPageTitleCorrect(String expectedTitle) {
        // Wait for the title to contain a part of the expected text, which is more robust
        // than waiting for the full title, which might load slowly.
        if (wait.until(ExpectedConditions.titleContains(expectedTitle))) {
            return driver.getTitle().equalsIgnoreCase(expectedTitle);
        }
        return false;
    }
}