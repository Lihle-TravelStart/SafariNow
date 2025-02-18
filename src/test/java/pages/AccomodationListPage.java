package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

public class AccomodationListPage {
    private WebDriver driver;
    WebDriverWait wait;


    public static By checkInDateInput = By.xpath("//input[@id='Checkin']");
    public By checkOutDateInput = By.xpath("//input[@id='Checkout']");

    public AccomodationListPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterCheckInDate(String checkInDate) {
        WebElement checkInField = driver.findElement(checkInDateInput);
        checkInField.clear();
        checkInField.sendKeys(checkInDate);
    }

    public void enterCheckOutDate(String checkOutDate) {
        WebElement checkOutField = driver.findElement(checkOutDateInput);
        checkOutField.clear();
        checkOutField.sendKeys(checkOutDate);
    }

    public void verifyPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        if (actualTitle.equalsIgnoreCase(expectedTitle)) {
            System.out.println("Title matches: " + actualTitle);
        } else {
            System.out.println("Title does not match. Expected: " + expectedTitle + ", Actual: " + actualTitle);
        }
    }

    // Method to click the 'Book' button on one of the properties
    public void clickBookButton(By propertyLocator) {
        List<WebElement> properties = driver.findElements(propertyLocator);
        if (!properties.isEmpty()) {
            WebElement bookButton = properties.get(0).findElement(By.xpath(".//button[contains(text(), 'Book')]")); // Adjust XPath as needed
            bookButton.click();
            System.out.println("Clicked 'Book' button for the property.");
        } else {
            System.out.println("No properties found to click the 'Book' button.");
        }
    }
}
        


