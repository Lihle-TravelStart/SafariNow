package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.NoAlertPresentException;

import java.time.Duration;
import java.util.List;

public class RepoPage {
    private final WebDriverWait wait;
    private final WebDriver driver;

    // --- Locators using @FindBy with existing CSS selectors ---
    @FindBy(css = "#frmMain > button:nth-child(29)")
    private WebElement refreshAvailabilityBtn;

    @FindBy(css = "#frmMain > button:nth-child(31)")
    private WebElement refreshLeadBtn;

    @FindBy(css = "#frmMain > button:nth-child(33)")
    private WebElement refreshListingsBtn;

    @FindBy(css = "#frmMain > button:nth-child(35)")
    private WebElement refreshPricingBtn;

    @FindBy(css = "#frmMain > button:nth-child(37)")
    private WebElement refreshSellerBtn;

    @FindBy(css = "#frmMain > button:nth-child(38)")
    private WebElement refreshMongoBtn;

    @FindBy(css = "#frmMain > button:nth-child(45)")
    private WebElement refreshSpecialsBtn;

    // Backup locators for more robust element finding
    private final By refreshAvailabilityBackup = By.xpath("//button[contains(text(), 'Refresh Availability') or contains(@value, 'Refresh Availability')]");
    private final By anyRefreshButton = By.xpath("//button[contains(text(), 'Refresh') or contains(@value, 'Refresh')]");

    // --- Constructor ---
    public RepoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // --- NEW: Alert Cleanup Methods ---

    /**
     * CRITICAL: Clear any existing alerts before performing actions.
     * This prevents "unexpected alert open" errors.
     */
    public void clearAnyExistingAlerts() {
        try {
            // Check if there's an alert present and dismiss it
            Alert existingAlert = driver.switchTo().alert();
            if (existingAlert != null) {
                String alertText = existingAlert.getText();
                System.out.println("⚠ Found existing alert with text: '" + alertText + "' - dismissing it");
                existingAlert.accept();
                Thread.sleep(500); // Wait for alert to close
                System.out.println("✓ Existing alert cleared");
            }
        } catch (NoAlertPresentException e) {
            // No alert present - this is good
            System.out.println("✓ No existing alerts to clear");
        } catch (Exception e) {
            System.err.println("⚠ Error clearing existing alerts: " + e.getMessage());
        }
    }

    /**
     * ENHANCED: Ensure all alerts are completely dismissed.
     */
    public void ensureNoAlertsPresent() {
        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                System.out.println("Found lingering alert (attempt " + (i + 1) + "): '" + alertText + "' - dismissing");
                alert.accept();
                Thread.sleep(500);
            } catch (NoAlertPresentException e) {
                System.out.println("✓ All alerts cleared after " + (i + 1) + " attempts");
                return;
            } catch (Exception e) {
                System.err.println("Error in alert cleanup attempt " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // --- Enhanced Verification Methods ---

    public boolean isRefreshAvailabilityBtnDisplayed() {
        System.out.println("=== DEBUGGING REFRESH AVAILABILITY BUTTON ===");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page title: " + driver.getTitle());

        debugPageStructure();

        try {
            System.out.println("Trying primary CSS selector: #frmMain > button:nth-child(29)");
            wait.until(ExpectedConditions.visibilityOf(refreshAvailabilityBtn));
            System.out.println("✓ Primary locator found the button successfully!");
            return true;

        } catch (Exception primaryException) {
            System.err.println("✗ Primary locator failed: " + primaryException.getMessage());
            return tryBackupLocators();
        }
    }

    /**
     * ENHANCED: Checks if a JavaScript alert is displayed.
     */
    public boolean isPopupDisplayed() {
        try {
            System.out.println("Checking for JavaScript alert...");

            // First, check if an alert is already present
            try {
                Alert existingAlert = driver.switchTo().alert();
                if (existingAlert != null) {
                    String alertText = existingAlert.getText();
                    System.out.println("✓ Alert already present with text: '" + alertText + "'");
                    return true;
                }
            } catch (NoAlertPresentException e) {
                System.out.println("No alert currently present, waiting for alert to appear...");
            }

            // If no alert is present, wait for one to appear
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if (alert != null) {
                String alertText = alert.getText();
                System.out.println("✓ Alert appeared with text: '" + alertText + "'");
                return true;
            }

            return false;

        } catch (UnhandledAlertException e) {
            System.out.println("✓ Alert detected via UnhandledAlertException: " + e.getMessage());
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error checking for alert: " + e.getMessage());
            return false;
        }
    }

    /**
     * ENHANCED: Accept the JavaScript alert with better error handling.
     */
    public void acceptAlert() {
        try {
            System.out.println("Attempting to accept alert...");

            Alert alert = null;

            // Try to get the alert
            try {
                alert = driver.switchTo().alert();
            } catch (NoAlertPresentException e) {
                System.out.println("Waiting for alert to appear...");
                alert = wait.until(ExpectedConditions.alertIsPresent());
            }

            if (alert != null) {
                String alertText = alert.getText();
                System.out.println("Alert text: '" + alertText + "'");
                alert.accept();
                System.out.println("✓ Alert accepted successfully");

                // Wait longer for alert to close completely
                Thread.sleep(1000);

                // Verify alert is actually gone
                ensureNoAlertsPresent();
            } else {
                System.err.println("✗ No alert found to accept");
            }

        } catch (Exception e) {
            System.err.println("✗ Failed to accept alert: " + e.getMessage());
            throw new RuntimeException("Failed to accept alert", e);
        }
    }

    public String getAlertText() {
        try {
            Alert alert = null;

            try {
                alert = driver.switchTo().alert();
            } catch (NoAlertPresentException e) {
                alert = wait.until(ExpectedConditions.alertIsPresent());
            }

            if (alert != null) {
                String text = alert.getText();
                System.out.println("Alert text retrieved: '" + text + "'");
                return text;
            }

            return null;

        } catch (Exception e) {
            System.err.println("✗ Failed to get alert text: " + e.getMessage());
            return null;
        }
    }

    private void debugPageStructure() {
        try {
            List<WebElement> frmMainElements = driver.findElements(By.id("frmMain"));
            System.out.println("Found " + frmMainElements.size() + " elements with id 'frmMain'");

            if (!frmMainElements.isEmpty()) {
                WebElement frmMain = frmMainElements.get(0);
                List<WebElement> buttons = frmMain.findElements(By.tagName("button"));
                System.out.println("Found " + buttons.size() + " buttons inside frmMain");

                for (int i = 0; i < Math.min(buttons.size(), 10); i++) {
                    WebElement button = buttons.get(i);
                    String text = button.getText().trim();
                    String value = button.getAttribute("value");
                    String id = button.getAttribute("id");
                    String className = button.getAttribute("class");

                    System.out.println("Button " + (i + 1) + ": text='" + text + "', value='" + value +
                            "', id='" + id + "', class='" + className + "'");
                }
            }

            List<WebElement> refreshButtons = driver.findElements(anyRefreshButton);
            System.out.println("Found " + refreshButtons.size() + " buttons containing 'Refresh' text");

            for (int i = 0; i < Math.min(refreshButtons.size(), 5); i++) {
                WebElement button = refreshButtons.get(i);
                System.out.println("Refresh button " + (i + 1) + ": '" + button.getText() + "'");
            }

        } catch (Exception e) {
            System.err.println("Error during page structure debugging: " + e.getMessage());
        }

        System.out.println("=== END DEBUGGING INFO ===");
    }

    private boolean tryBackupLocators() {
        System.out.println("Trying backup locators...");

        try {
            System.out.println("Trying backup XPath locator...");
            WebElement backupElement = wait.until(ExpectedConditions.visibilityOfElementLocated(refreshAvailabilityBackup));
            System.out.println("✓ Backup locator found the button: " + backupElement.getText());
            return true;

        } catch (Exception backupException) {
            System.err.println("✗ Backup locator also failed: " + backupException.getMessage());

            try {
                List<WebElement> anyRefreshButtons = driver.findElements(anyRefreshButton);
                if (!anyRefreshButtons.isEmpty()) {
                    System.out.println("Found " + anyRefreshButtons.size() + " refresh buttons, but none specifically for 'Availability'");
                    return false;
                } else {
                    System.err.println("No refresh buttons found at all - likely not on the correct page");
                    return false;
                }
            } catch (Exception finalException) {
                System.err.println("Final attempt failed: " + finalException.getMessage());
                return false;
            }
        }
    }

    // --- ENHANCED Action Methods ---

    /**
     * CRITICAL FIX: Enhanced helper that clears alerts before clicking.
     */
    private void clickButton(WebElement button, String buttonName) {
        try {
            System.out.println("=== Clicking " + buttonName + " Button ===");

            // STEP 1: Clear any existing alerts first
            System.out.println("Step 1: Clearing any existing alerts...");
            clearAnyExistingAlerts();

            // STEP 2: Wait for element to be clickable
            System.out.println("Step 2: Waiting for button to be clickable...");
            WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(button));

            // STEP 3: Scroll to element if needed
            System.out.println("Step 3: Scrolling to button...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clickableButton);
            Thread.sleep(500);

            // STEP 4: Click the button
            System.out.println("Step 4: Clicking the button...");
            clickableButton.click();
            System.out.println("✓ Successfully clicked " + buttonName + " button");

            // STEP 5: Wait for alert to appear
            System.out.println("Step 5: Waiting for alert to appear...");
            Thread.sleep(1500); // Increased wait time for alert

        } catch (UnhandledAlertException e) {
            // If we get this exception, it means the click worked and an alert appeared
            System.out.println("✓ Button click successful - alert appeared: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("✗ Failed to click " + buttonName + " button: " + e.getMessage());

            // Try JavaScript click as fallback
            try {
                System.out.println("Trying JavaScript click for " + buttonName + "...");
                clearAnyExistingAlerts(); // Clear alerts before JS click too
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                System.out.println("✓ JavaScript click succeeded for " + buttonName);
                Thread.sleep(1500);
            } catch (Exception jsException) {
                throw new RuntimeException("Failed to click " + buttonName + " with both regular and JavaScript clicks", e);
            }
        }
    }

    // --- Public Action Methods ---
    public void clickRefreshAvailabilityBtn() {
        clickButton(refreshAvailabilityBtn, "Refresh Availability");
    }

    public void clickRefreshLeadBtn() {
        clickButton(refreshLeadBtn, "Refresh Lead");
    }

    public void clickRefreshListingsBtn() {
        clickButton(refreshListingsBtn, "Refresh Listings");
    }

    public void clickRefreshPricingBtn() {
        clickButton(refreshPricingBtn, "Refresh Pricing");
    }

    public void clickRefreshSellerBtn() {
        clickButton(refreshSellerBtn, "Refresh Seller");
    }

    public void clickRefreshMongoBtn() {
        clickButton(refreshMongoBtn, "Refresh Mongo");
    }

    public void clickRefreshSpecialsBtn() {
        clickButton(refreshSpecialsBtn, "Refresh Specials");
    }

    // --- Additional Utility Methods ---

    public boolean waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("frmMain")));
            System.out.println("✓ Page loaded - frmMain element found");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Page load timeout - frmMain element not found: " + e.getMessage());
            return false;
        }
    }

    public int getButtonCount() {
        try {
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            System.out.println("Total buttons found on page: " + buttons.size());
            return buttons.size();
        } catch (Exception e) {
            System.err.println("Error counting buttons: " + e.getMessage());
            return 0;
        }
    }
}
