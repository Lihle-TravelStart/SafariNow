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

    @FindBy(xpath = "//*[@id='MenuAccount']/a/span")
    private WebElement menuAccountButton;

    @FindBy(id = "MenuItems")
    private WebElement menuItemsContainer;

    @FindBy(css = "#MenuItems a[href='/Logon.aspx']")
    private WebElement loginLink;

    @FindBy(xpath = "//*[@id='MenuItems']//a[translate(normalize-space(), 'LOGOUT', 'logout')='logout']")
    private WebElement logoutLink;

    @FindBy(id = "search-destination")
    private WebElement destinationInput;

    @FindBy(css = ".autocomplete-suggestions .autocomplete-suggestion")
    private List<WebElement> destinationSuggestions;

    @FindBy(id = "search-button")
    private WebElement searchButton;

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

    public void enterDestination(String destination) {
        wait.until(ExpectedConditions.visibilityOf(destinationInput)).clear();
        destinationInput.sendKeys(destination);
        System.out.println(STR."Entered destination: \{destination}");
    }

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

    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        System.out.println("Clicked 'Search' button.");
    }

    public void performSearch(String destination) {
        System.out.println(STR."--- Performing search for: \{destination} ---");
        enterDestination(destination);
        selectSuggestion(destination);
        clickSearchButton();
    }

    // --- Carousel Locators and Methods (IMPROVED) ---

    /**
     * IMPROVED: Finds the carousel container using a more flexible XPath.
     * It looks for the swiper container that is a general sibling following the h3 title,
     * which is more robust against minor changes in the HTML structure.
     */
    public By getCarouselContainerByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@id, '-swiperContainer')][1]", labelText));
    }

    /**
     * IMPROVED: Finds the 'Next' button within the context of the general sibling carousel.
     */
    public By getCarouselNextButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@class, 'swiper-button-next')][1]", labelText));
    }

    /**
     * IMPROVED: Finds the 'Previous' button within the context of the general sibling carousel.
     */
    public By getCarouselPrevButtonByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@class, 'swiper-button-prev')][1]", labelText));
    }

    /**
     * IMPROVED: Finds the visible, non-duplicate carousel items using the more flexible XPath.
     */
    public By getCarouselItemsByLabel(String labelText) {
        return By.xpath(String.format("//h3[normalize-space()='%s']/following::div[contains(@id, '-swiperContainer')][1]//div[contains(@class, 'swiper-slide') and not(contains(@class, 'swiper-slide-duplicate')) and string-length(normalize-space(.)) > 0 and not(contains(@style, 'display: none')) and not(contains(@aria-hidden, 'true'))]", labelText));
    }

    public boolean isCarouselPresent(String carouselName) {
        By containerLocator = getCarouselContainerByLabel(carouselName);
        By itemsLocator = getCarouselItemsByLabel(carouselName);
        try {
            // Add a small explicit wait right after scrolling to give the DOM time to update.
            Thread.sleep(500); // 0.5 second pause
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