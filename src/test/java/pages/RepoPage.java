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

    // Locators for elements on the Repo Page (Popup)
    private By popup = By.id("popupId"); // Example, change to actual locator
    private By okButton = By.xpath("//button[contains(text(),'OK')]"); // Example XPath, adjust as needed
    private By refreshAvailabilityBtn = By.cssSelector("#frmMain > button:nth-child(29)");
    private By refreshLeadBtn = By.cssSelector("#frmMain > button:nth-child(31)");
    private By refreshLsitingsBtn = By.cssSelector("#frmMain > button:nth-child(33)");
    private By refreshPricingBtn = By.cssSelector("#frmMain > button:nth-child(35)");
    private By refreshSellerBtn = By.cssSelector("#frmMain > button:nth-child(37)");
    private By refreshMongoBtn = By.cssSelector("#frmMain > button:nth-child(38)");
    private By refreshSpecialsBtn = By.cssSelector("#frmMain > button:nth-child(45)");

    public void clickRefreshLeadBtn() {
        WebElement refreshLead = driver.findElement(refreshLeadBtn);
        refreshLead.click();
    }
    public void clickRefreshListingsBtn() {
        WebElement refreshListings = driver.findElement(refreshLsitingsBtn);
        refreshListings.click();
    }
    public void clickRefreshPricingBtn() {
        WebElement refreshPricing = driver.findElement(refreshPricingBtn);
        refreshPricing.click();
    }
    public void clickRefreshSellerBtn() {
        WebElement refreshSeller = driver.findElement(refreshSellerBtn);
        refreshSeller.click();
    }
    public void clickRefreshMongoBtn() {
        WebElement refreshMongo = driver.findElement(refreshMongoBtn);
        refreshMongo.click();
    }
    public void clickRefreshSpecialsBtn() {
        WebElement refreshSpecials = driver.findElement(refreshSpecialsBtn);
        refreshSpecials.click();
    }
    public void clickRefreshAvailabilityBtn() {
        WebElement refreshAvailability = driver.findElement(refreshAvailabilityBtn);
        refreshAvailability.click();
    }

    private By popupText = By.xpath("//div[@class='popup-text']"); // Example XPath to find the text


    // Constructor
    public RepoPage(WebDriver driver) {
        this.driver = driver;
    }

    // Method to check if popup is displayed
    public boolean isPopupDisplayed() {
        try {
            // Wait for the popup to be visible (maximum 1 minute)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.alertIsPresent());  // Wait for the alert to be present

            // Switch to the alert and get the text
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert Text: " + alert.getText());

            // Accept the alert (click "OK")
            alert.accept();

            return true;
        } catch (Exception e) {
            // If the popup isn't found, print and return false
            System.out.println("Popup not displayed.");
            return false;
        }
    }

    // Method to click the OK button on the popup
    public void clickOkButton() {
        if (isPopupDisplayed()) {
            WebElement okBtn = driver.findElement(okButton);
            okBtn.click();
        } else {
            System.out.println("Popup not displayed");
        }
    }

    // Method to read the numbers displayed on the popup
    public String getPopupNumber() {
        if (isPopupDisplayed()) {
            WebElement popupTextElement = driver.findElement(popupText);
            String popupContent = popupTextElement.getText();

            // Regular expression to extract numbers
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(popupContent);

            if (matcher.find()) {
                return matcher.group(); // Returns the first number found
            } else {
                return "No numbers found in popup text.";
            }
        } else {
            return "Popup not displayed.";
        }
    }


}
