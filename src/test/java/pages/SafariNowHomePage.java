package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Encapsulates all elements and actions for the SafariNow homepage.
 * This includes navigation, search, login/logout, and carousel interactions.
 */
public class SafariNowHomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- Web Elements ---

    @FindBy(id = "cookie-consent-button")
    private WebElement cookieConsentButton;

    @FindBy(css = "#newsletter-modal .close")
    private WebElement newsletterModalCloseButton;

    // This is the one and only destination input field on the homepage.
    @FindBy(id = "SearchFilterFilterSearchTerm")
    private WebElement destinationInput;

    // NOTE: This @FindBy is a general locator and is NOT used by the robust
    // selectSuggestion method, which performs its own dynamic lookups to avoid stale elements.
    @FindBy(css = ".tt-suggestion")
    private List<WebElement> destinationSuggestions;

    @FindBy(id = "search-button")
    private WebElement searchButton;

    @FindBy(xpath = "//*[@id='MenuAccount']/a/span")
    private WebElement menuAccountButton;

    @FindBy(id = "MenuItems")
    private WebElement menuItemsContainer;

    @FindBy(css = "#MenuItems a[href='/Logon.aspx']")
    private WebElement loginLink;

    @FindBy(xpath = "//*[@id='MenuItems']//a[translate(normalize-space(), 'LOGOUT', 'logout')='logout']")
    private WebElement logoutLink;

    @FindBy(css = "#MenuLogo > a")
    private WebElement logoLink;

    // --- Constructor ---

    public SafariNowHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // --- Navigation and General Actions ---

    public void navigateToUrl(String url) {
        driver.get(url);
        System.out.println(STR."Navigating to: \{url}");
    }

    public void returnToHomePageByLogo() {
        wait.until(ExpectedConditions.elementToBeClickable(logoLink)).click();
        System.out.println("✓ Navigated to homepage by clicking the logo.");
    }

    /**
     * A robust method to handle any potential overlays that might
     * block interaction, such as cookie banners or promotional pop-ups.
     */
    public void handleOverlays() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            WebElement bannerButton = shortWait.until(ExpectedConditions.elementToBeClickable(cookieConsentButton));
            bannerButton.click();
            wait.until(ExpectedConditions.invisibilityOf(bannerButton));
            System.out.println("✓ Cookie consent banner closed.");
        } catch (Exception e) {
            System.out.println("Info: Cookie consent banner was not present or did not need closing.");
        }

        try {
            WebElement modalButton = shortWait.until(ExpectedConditions.elementToBeClickable(newsletterModalCloseButton));
            modalButton.click();
            wait.until(ExpectedConditions.invisibilityOf(modalButton));
            System.out.println("✓ Promotional pop-up closed.");
        } catch (Exception e) {
            System.out.println("Info: Promotional pop-up was not present or did not need closing.");
        }
    }

    // --- Login/Logout Actions ---

    public void clickMenuAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(menuAccountButton)).click();
        System.out.println("✓ Clicked 'Account' menu button.");
    }

    public void clickLoginLink() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#MenuItems a[href='/Logon.aspx']")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", loginLink);
            System.out.println("✓ Clicked 'Login' link via JavaScript.");
        } catch (Exception e) {
            System.err.println("Failed to click login link. Trying a standard click as a fallback...");
            wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
            System.out.println("✓ Clicked 'Login' link via standard click.");
        }
    }

    public void clickLogoutLink() {
        wait.until(ExpectedConditions.visibilityOf(menuItemsContainer));
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        System.out.println("✓ Clicked 'Logout' link.");
        wait.until(ExpectedConditions.visibilityOf(loginLink));
    }

    // --- Search Actions (Complete Implementation) ---

    /**
     * REFACTORED: This method now types like a human to reliably trigger the autocomplete JavaScript.
     */
    public void enterDestination(String destination) {
        System.out.println("Waiting for the destination input field to be clickable...");
        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(destinationInput));
        inputField.click();
        System.out.println("✓ Clicked destination input field.");
        inputField.clear();

        System.out.println(STR."Typing destination slowly to trigger suggestions: \{destination}");
        for (char c : destination.toCharArray()) {
            inputField.sendKeys(String.valueOf(c));
            try {
                // This small pause is critical for mimicking human typing.
                Thread.sleep(75); // 75 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Typing was interrupted", e);
            }
        }
        System.out.println(STR."✓ Finished typing: \{destination}");
    }

    /**
     * REFACTORED: This method now uses the exact locators from the provided HTML.
     * It waits for the suggestions to be present, finds the first exact match, and clicks it.
     */
    public void selectSuggestion(String suggestionText) {
        // CORRECTED: This locator now perfectly matches the HTML structure for each suggestion item.
        By suggestionItemLocator = By.cssSelector(".tt-suggestion.tt-selectable");

        try {
            System.out.println("Waiting for search suggestions to be present in the DOM...");
            // Step 1: Wait for the suggestion items to exist in the DOM. This should now pass.
            List<WebElement> availableSuggestions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(suggestionItemLocator));
            System.out.println(STR."✓ Found \{availableSuggestions.size()} suggestions in DOM.");

            // Step 2: Iterate through the suggestions to find the first exact match.
            for (WebElement suggestionItem : availableSuggestions) {
                try {
                    // Find the element with the text *inside* the current suggestion item.
                    WebElement nameElement = suggestionItem.findElement(By.cssSelector(".suggestion-name"));
                    String currentSuggestionText = nameElement.getText();

                    // Check for an exact, case-insensitive match.
                    if (currentSuggestionText.equalsIgnoreCase(suggestionText)) {
                        System.out.println(STR."Found matching suggestion: '\{currentSuggestionText}'. Clicking it.");
                        // Step 3: Use a JavaScript click for maximum reliability.
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", suggestionItem);
                        return; // Exit the method successfully.
                    }
                } catch (NoSuchElementException ex) {
                    // This can happen if a suggestion div is a separator or something without a name.
                    System.out.println("Info: Found a suggestion item without a '.suggestion-name' child. Skipping.");
                }
            }

            // If the loop finishes without finding a match, throw an exception.
            throw new NoSuchElementException(STR."After checking \{availableSuggestions.size()} items, an exact match for '\{suggestionText}' was not found.");

        } catch (Exception e) {
            // Provide a more detailed error message if anything goes wrong.
            throw new RuntimeException(STR."Failed to select suggestion '\{suggestionText}': \{e.getMessage()}", e);
        }
    }

    /**
     * This method is no longer needed for the primary search flow but is kept
     * in case there are other scenarios where it might be used.
     */
    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        System.out.println("Clicked 'Search' button.");
    }

    /**
     * The main user-facing search action.
     * It now correctly reflects that selecting a suggestion is the final step.
     */
    public void performSearch(String destination) {
        System.out.println(STR."--- Performing search for: \{destination} ---");
        enterDestination(destination);
        selectSuggestion(destination);
        // The clickSearchButton() call is removed because selecting a suggestion triggers the search.
        System.out.println("✓ Search initiated by selecting a suggestion.");
    }

    // --- Carousel Locators and Methods ---

    public By getCarouselContainerByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@id, '-swiperContainer')][1]", labelText));
    }

    public By getCarouselNextButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@class, 'swiper-button-next')][1]", labelText));
    }

    public By getCarouselPrevButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@class, 'swiper-button-prev')][1]", labelText));
    }

    public By getCarouselItemsByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@id, '-swiperContainer')][1]//div[contains(@class, 'swiper-slide') and not(contains(@class, 'swiper-slide-duplicate')) and string-length(normalize-space(.)) > 0 and not(contains(@style, 'display: none')) and not(contains(@aria-hidden, 'true'))]", labelText));
    }

    public boolean isCarouselPresent(String carouselName) {
        By containerLocator = getCarouselContainerByLabel(carouselName);
        By itemsLocator = getCarouselItemsByLabel(carouselName);
        try {
            Thread.sleep(500); // Small pause for DOM to update after scroll
            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(containerLocator));
            List<WebElement> items = driver.findElements(itemsLocator);
            return container.isDisplayed() && !items.isEmpty();
        } catch (TimeoutException | NoSuchElementException e) {
            System.err.println(STR."Carousel '\{carouselName}' not found or not visible: \{e.getMessage()}");
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}