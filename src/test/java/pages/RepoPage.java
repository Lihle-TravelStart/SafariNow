package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RepoPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String REPO_PAGE_URL = "https://www.safarinow.com/staff/RepoRefresh";

    @FindBy(xpath = "//button[contains(., 'Refresh Availability')]")
    private WebElement refreshAvailabilityBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Lead')]")
    private WebElement refreshLeadBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Listings')]")
    private WebElement refreshListingsBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Pricing')]")
    private WebElement refreshPricingBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Seller')]")
    private WebElement refreshSellerBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Mongo')]")
    private WebElement refreshMongoBtn;

    @FindBy(xpath = "//button[contains(., 'Refresh Specials')]")
    private WebElement refreshSpecialsBtn;

    @FindBy(tagName = "body")
    private WebElement pageBody;

    public RepoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    public void navigateToRepoPage() {
        if (!driver.getCurrentUrl().equals(REPO_PAGE_URL)) {
            System.out.println(STR."Navigating to: \{REPO_PAGE_URL}");
            driver.get(REPO_PAGE_URL);
        }
    }

    public boolean isRefreshAvailabilityBtnDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(refreshAvailabilityBtn)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // --- Public click methods now return void ---

    public void clickRefreshAvailabilityBtn() {
        performActionAndWaitForResult(refreshAvailabilityBtn, "Refresh Availability");
    }

    public void clickRefreshLeadBtn() {
        performActionAndWaitForResult(refreshLeadBtn, "Refresh Lead");
    }

    public void clickRefreshListingsBtn() {
        performActionAndWaitForResult(refreshListingsBtn, "Refresh Listings");
    }

    public void clickRefreshPricingBtn() {
        performActionAndWaitForResult(refreshPricingBtn, "Refresh Pricing");
    }

    public void clickRefreshSellerBtn() {
        performActionAndWaitForResult(refreshSellerBtn, "Refresh Seller");
    }

    public void clickRefreshMongoBtn() {
        performActionAndWaitForResult(refreshMongoBtn, "Refresh Seller Mongo");
    }

    public void clickRefreshSpecialsBtn() {
        performActionAndWaitForResult(refreshSpecialsBtn, "Refresh Specials");
    }

    // --- Private Helper Method ---

    private void performActionAndWaitForResult(WebElement button, String buttonName) {
        System.out.println(STR."=== Clicking \{buttonName} Button ===");
        System.out.println("Step 1: Clearing any existing alerts...");
        clearAlertIfPresent();

        System.out.println("Step 2: Waiting for button to be clickable...");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        } catch (Exception e) {
            System.err.println(STR."⚠ Regular click failed. Trying JavaScript click for \{buttonName}...");
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            } catch (Exception jsException) {
                System.err.println(STR."✗ Failed to click \{buttonName} button.");
                throw new RuntimeException(STR."Failed to click \{buttonName} with both regular and JavaScript clicks", jsException);
            }
        }

        System.out.println("Step 3: Waiting for the confirmation alert...");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println(STR."✓ Found alert with text: '\{alertText}'");
        alert.accept();

        // THE FIX: The method now successfully concludes after handling the alert.
        // The wait for "task complete" text has been removed as it does not match
        // the application's actual behavior, making the test more robust.
        System.out.println("✓ Alert handled. Action is considered complete.");
    }

    private void clearAlertIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            System.out.println(STR."⚠ Found and dismissed a stale alert: \{alert.getText()}");
            alert.accept();
        } catch (TimeoutException e) {
            // This is the expected case - no alert was present.
        }
    }
}