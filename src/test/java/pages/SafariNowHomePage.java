package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

    @FindBy(xpath = "//*[@id='MenuAccount']/a/span")
    private WebElement menuAccountButton;

    @FindBy(id = "MenuItems")
    private WebElement menuItemsContainer;

    /**
     * CORRECTED: This locator is now a precise and robust CSS selector targeting the link's href attribute.
     * It is much more reliable than matching by text.
     */
    @FindBy(css = "#MenuItems a[href='/Logon.aspx']")
    private WebElement loginLink;

    /**
     * IMPROVED: This locator is now case-insensitive to prevent future errors.
     */
    @FindBy(xpath = "//*[@id='MenuItems']//a[translate(normalize-space(), 'LOGOUT', 'logout')='logout']")
    private WebElement logoutLink;

    @FindBy(id = "search-destination")
    private WebElement destinationInput;

    @FindBy(css = ".autocomplete-suggestions .autocomplete-suggestion")
    private List<WebElement> destinationSuggestions;

    @FindBy(id = "search-button")
    private WebElement searchButton;
    /**
     * ADDED: The main SafariNow logo, used to navigate back to the homepage.
            */
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
    /**
     * ADDED: A user-centric way to return to the homepage.
     * Clicks the main site logo.
     */
    public void returnToHomePageByLogo() {
        wait.until(ExpectedConditions.elementToBeClickable(logoLink)).click();
        System.out.println("✓ Navigated to homepage by clicking the logo.");
    }


    public void handleCookieBanner() {
        try {
            wait.until(ExpectedConditions.visibilityOf(cookieConsentButton));
            cookieConsentButton.click();
            wait.until(ExpectedConditions.invisibilityOf(cookieConsentButton));
            System.out.println("✓ Cookie consent banner closed.");
        } catch (Exception e) {
            System.out.println("Info: Cookie consent banner was not present or failed to close.");
        }
    }

    // --- Login/Logout Actions ---

    public void clickMenuAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(menuAccountButton)).click();
        System.out.println("✓ Clicked 'Account' menu button.");
    }

    public void clickLoginLink() {
        wait.until(ExpectedConditions.visibilityOf(menuItemsContainer));
        wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
        System.out.println("✓ Clicked 'Login' link.");
    }

    public void clickLogoutLink() {
        wait.until(ExpectedConditions.visibilityOf(menuItemsContainer));
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        System.out.println("✓ Clicked 'Logout' link.");
        // After logout, confirm by waiting for the login link to be visible again.
        wait.until(ExpectedConditions.visibilityOf(loginLink));
    }

    // --- Search Actions (Complete Implementation) ---

    /**
     * Enters the given text into the destination search input field.
     * @param destination The text to enter.
     */
    public void enterDestination(String destination) {
        wait.until(ExpectedConditions.visibilityOf(destinationInput)).clear();
        destinationInput.sendKeys(destination);
        System.out.println(STR."Entered destination: \{destination}");
    }

    /**
     * Waits for the autocomplete suggestions to appear and selects the one matching the text.
     * @param suggestionText The exact text of the suggestion to select.
     */
    public void selectSuggestion(String suggestionText) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(destinationSuggestions));
            WebElement suggestion = destinationSuggestions.stream()
                    .filter(s -> s.getText().equalsIgnoreCase(suggestionText))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(STR."Suggestion '\{suggestionText}' not found."));
            suggestion.click();
            System.out.println(STR."Selected suggestion: \{suggestionText}");
        } catch (Exception e) {
            throw new RuntimeException(STR."Failed to select suggestion '\{suggestionText}': \{e.getMessage()}", e);
        }
    }

    /**
     * Clicks the main search button to submit the search.
     */
    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        System.out.println("Clicked 'Search' button.");
    }

    /**
     * Performs a complete search action from entering the destination to clicking search.
     * This is the high-level business method used by your tests.
     * @param destination The destination to search for.
     */
    public void performSearch(String destination) {
        System.out.println(STR."--- Performing search for: \{destination} ---");
        enterDestination(destination);
        selectSuggestion(destination);
        clickSearchButton();
    }

    // --- Carousel Locators and Methods ---

    public By getCarouselContainerByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following-sibling::div[contains(@id, '-swiperContainer')]", labelText));
    }

    public By getCarouselNextButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following-sibling::div//div[contains(@class, 'swiper-button-next')]", labelText));
    }

    public By getCarouselPrevButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following-sibling::div//div[contains(@class, 'swiper-button-prev')]", labelText));
    }

    public By getCarouselItemsByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following-sibling::div[contains(@id, '-swiperContainer')]//div[contains(@class, 'swiper-slide') and not(contains(@class, 'swiper-slide-duplicate')) and string-length(normalize-space(.)) > 0 and not(contains(@style, 'display: none')) and not(contains(@aria-hidden, 'true'))]", labelText));
    }

    public boolean isCarouselPresent(String carouselName) {
        By containerLocator = getCarouselContainerByLabel(carouselName);
        By itemsLocator = getCarouselItemsByLabel(carouselName);
        try {
            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(containerLocator));
            List<WebElement> items = driver.findElements(itemsLocator);
            return container.isDisplayed() && !items.isEmpty();
        } catch (TimeoutException | NoSuchElementException e) {
            System.err.println(STR."Carousel '\{carouselName}' not found or not visible: \{e.getMessage()}");
            return false;
        }
    }
}