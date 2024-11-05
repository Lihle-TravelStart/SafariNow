package pages;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

public class AccomodationResultsPage {

    private WebDriver driver;
    WebDriverWait wait;
    public AccomodationResultsPage(WebDriver driver) {
        this.driver = driver;
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
