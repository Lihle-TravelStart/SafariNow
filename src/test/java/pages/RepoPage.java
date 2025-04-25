package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepoPage {
    private WebDriver driver;
    // Locators for elements on the Repo Page
    private static final String POPUP_ID = "popupId";
    private static final String OK_BUTTON_XPATH = "//button[contains(text(),'OK')]";
    private static final String REFRESH_AVAILABILITY_BUTTON_CSS = "#frmMain > button:nth-child(29)";
    private static final String REFRESH_LEAD_BUTTON_CSS = "#frmMain > button:nth-child(31)";
    private static final String REFRESH_LISTINGS_BUTTON_CSS = "#frmMain > button:nth-child(33)";
    private static final String REFRESH_PRICING_BUTTON_CSS = "#frmMain > button:nth-child(35)";
    private static final String REFRESH_SELLER_BUTTON_CSS = "#frmMain > button:nth-child(37)";
    private static final String REFRESH_MONGO_BUTTON_CSS = "#frmMain > button:nth-child(38)";
    private static final String REFRESH_SPECIALS_BUTTON_CSS = "#frmMain > button:nth-child(45)";
    private static final String POPUP_TEXT_XPATH = "//div[@class='popup-text']";
    private final WebDriverWait wait;

    // Constructor
    public RepoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // Initialize WebDriverWait
    }

    // Refactored method to click refresh buttons
    private void clickRefreshButton(By locator, String buttonName) {
        try {
            WebElement button = driver.findElement(locator);
            button.click();
            System.out.println("Clicked on the " + buttonName + " button.");
        } catch (Exception e) {
            System.err.println("Error clicking the " + buttonName + " button: " + e.getMessage());
        }
    }

    public void clickRefreshLeadBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_LEAD_BUTTON_CSS), "Refresh Lead");
    }

    public void clickRefreshListingsBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_LISTINGS_BUTTON_CSS), "Refresh Listings");
    }

    public void clickRefreshPricingBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_PRICING_BUTTON_CSS), "Refresh Pricing");
    }

    public void clickRefreshSellerBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_SELLER_BUTTON_CSS), "Refresh Seller");
    }

    public void clickRefreshMongoBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_MONGO_BUTTON_CSS), "Refresh Mongo");
    }

    public void clickRefreshSpecialsBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_SPECIALS_BUTTON_CSS), "Refresh Specials");
    }

    public void clickRefreshAvailabilityBtn() {
        clickRefreshButton(By.cssSelector(REFRESH_AVAILABILITY_BUTTON_CSS), "Refresh Availability");
    }

    // Method to check if an alert is displayed and accept it.
    public boolean isPopupDisplayed() {
        try {
            // Wait for the alert to be present
            wait.until(ExpectedConditions.alertIsPresent());

            // Switch to the alert and get the text
            Alert alert = driver.switchTo().alert();
            System.out.println("Refresedh records: " + alert.getText());

            // Accept the alert (click "OK")
            alert.accept();
            System.out.println("Alert accepted.");
            return true;
        } catch (Exception e) {
            // If the popup isn't found, print and return false
            System.err.println("Popup not displayed or error handling alert: " + e.getMessage());
            return false;
        }
    }

    // Method to click the OK button on the popup
    public void clickOkButton() {
        By okButton = By.xpath(OK_BUTTON_XPATH);
        if (isPopupDisplayed()) {
            try {
                WebElement okBtn = driver.findElement(okButton);
                okBtn.click();
                System.out.println("OK button clicked.");
            } catch (Exception e) {
                System.err.println("Could not click on Ok button: " + e.getMessage());
            }
        } else {
            System.out.println("Popup not displayed");
        }
    }

    // Method to read the numbers displayed on the popup
    public String getPopupNumber() {
        if (isPopupDisplayed()) {
            try {
                WebElement popupTextElement = driver.findElement(By.xpath(POPUP_TEXT_XPATH));
                String popupContent = popupTextElement.getText();

                // Regular expression to extract numbers
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(popupContent);

                if (matcher.find()) {
                    String number = matcher.group();
                    System.out.println(number + " Records updated");
                    return number;
                } else {
                    System.out.println("No numbers found in popup text.");
                    return "No numbers found in popup text.";
                }
            } catch (Exception e) {
                System.err.println("Could not get popup number: " + e.getMessage());
                return "Could not get popup number";
            }
        } else {
            System.out.println("Popup not displayed.");
            return "Popup not displayed.";
        }
    }
}