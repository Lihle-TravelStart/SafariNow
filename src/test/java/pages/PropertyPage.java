package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Encapsulates all elements and actions for a single property's detail page.
 * This class provides methods to interact with date pickers, room selection,
 * and booking/quote actions in a stable and reusable way.
 */
public class PropertyPage {

    private final WebDriverWait wait;

    // --- Web Elements ---

    // The main heading of the property page, which serves as its title.
    @FindBy(tagName = "h1")
    private WebElement pageTitleHeader;

    // The button to trigger the availability check after setting dates/rooms.
    @FindBy(id = "btnCheckAvailability")
    private WebElement checkAvailabilityButton;

    // The button to proceed with an instant booking.
    @FindBy(id = "btnBookNow")
    private WebElement bookNowButton;

    // The button to request a quote instead of booking instantly.
    @FindBy(id = "btnRequestQuote")
    private WebElement requestQuoteButton;

    // A container that appears after a successful availability check, holding room details.
    @FindBy(css = ".room-details-container")
    private WebElement roomDisplayedContainer;

    // --- Constructor ---

    public PropertyPage(WebDriver driver) {
        // Use a longer wait time for property pages as they can be content-heavy.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // --- Page Actions & Verifications ---

    /**
     * Retrieves the main title of the property page from its <h1> header.
     * It waits for the header to be visible before returning its text.
     *
     * @return The text of the page's main title.
     */
    public String getPageTitle() {
        System.out.println("Waiting for property page title to be visible...");
        String title = wait.until(ExpectedConditions.visibilityOf(pageTitleHeader)).getText();
        System.out.println(STR."✓ Page title is: '\{title}'");
        return title;
    }

    /**
     * A placeholder for the complex logic of selecting a room configuration.
     * This would typically involve clicking a dropdown and selecting an option.
     *
     * @param roomConfiguration The desired room setup (e.g., "1 Room, 2 Adults").
     */
    public void selectRoom(String roomConfiguration) {
        // In a real-world scenario, this would involve interacting with the rooms dropdown.
        // For now, we log the action as a placeholder.
        // Example: driver.findElement(By.id("rooms-dropdown")).click();
        //          driver.findElement(By.xpath(String.format("//li[text()='%s']", roomConfiguration))).click();
        System.out.println(STR."Placeholder: Selecting room configuration '\{roomConfiguration}'.");
    }

    /**
     * Clicks the 'Check Availability' button after dates and rooms have been set.
     * Waits for the button to be clickable before acting.
     */
    public void clickCheckAvailability() {
        System.out.println("Waiting for 'Check Availability' button to be clickable...");
        wait.until(ExpectedConditions.elementToBeClickable(checkAvailabilityButton)).click();
        System.out.println("✓ Clicked 'Check Availability' button.");
    }

    /**
     * Verifies if the room details section is displayed after an availability check.
     *
     * @return true if the room container is visible, false otherwise.
     */
    public boolean isRoomDisplayed() {
        try {
            System.out.println("Checking for the presence of the room details container...");
            return wait.until(ExpectedConditions.visibilityOf(roomDisplayedContainer)).isDisplayed();
        } catch (Exception e) {
            System.err.println("Room details container was not found or not visible.");
            return false;
        }
    }

    /**
     * Clicks the 'Book Now' button, typically after rooms have been displayed.
     * Waits for the button to be clickable.
     */
    public void clickBookNow() {
        System.out.println("Waiting for 'Book Now' button to be clickable...");
        wait.until(ExpectedConditions.elementToBeClickable(bookNowButton)).click();
        System.out.println("✓ Clicked 'Book Now' button.");
    }

    /**
     * Clicks the 'Request Quote' button, typically after rooms have been displayed.
     * Waits for the button to be clickable.
     */
    public void clickRequestQuote() {
        System.out.println("Waiting for 'Request Quote' button to be clickable...");
        wait.until(ExpectedConditions.elementToBeClickable(requestQuoteButton)).click();
        System.out.println("✓ Clicked 'Request Quote' button.");
    }
}