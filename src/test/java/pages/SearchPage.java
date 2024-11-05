package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchPage {
    private WebDriver driver;
    WebDriverWait wait;

    // Locators
    public By destinationInput = By.id("SearchFilterFilterSearchTerm"); // Replace with actual locator
    private By checkInDateInput = By.id("check-in"); // Replace with actual locator
    private By checkOutDateInput = By.id("check-out"); // Replace with actual locator
    private By searchButton = By.id("btnSearch"); // Replace with actual locator
    public By suggestionListLocator = By.xpath("//*[@id='scrollable-dropdown-menu']/span/div/div/div[2]/div/div[1]");
    private By suggestionItemsLocator = By.xpath("//ul[contains(@class, 'suggestions-list')]/li");
    private By popularAccommodationLocator = By.cssSelector(".popular-accommodations .accommodation-item");
    private By bookOrEnquireButtonLocator = By.xpath(".//button[contains(text(), 'Book Now') or contains(text(), 'Enquire')]");
                                                 //*[@id="scrollable-dropdown-menu"]/span/div/div/div[2]/div/div[1]
    public SearchPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterDestination(String destination) {
        WebElement destinationField = driver.findElement(destinationInput);
        destinationField.clear();
        destinationField.sendKeys(destination);
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

    public void clickSearchButton() {
        driver.findElement(searchButton).click();
    }

   /* public void selectAutoSuggestion(String location) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionListLocator));

        List<WebElement> suggestions = driver.findElements(suggestionItemsLocator);

        for (WebElement suggestion : suggestions) {
            String suggestionText = suggestion.getText();
            if (suggestionText.contains(location)) {
                suggestion.click();
                System.out.println("Selected suggestion: " + suggestionText);
                break;
            }
        }
    }      */
    public void selectPopularAccommodation(int propertyIndex) {
        // Wait until the popular accommodations section is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(popularAccommodationLocator));

        // Get the list of popular accommodations
        List<WebElement> popularAccommodations = driver.findElements(popularAccommodationLocator);

        // Check if the index is within bounds
        if (propertyIndex < 0 || propertyIndex >= popularAccommodations.size()) {
            throw new IllegalArgumentException("Invalid property index");
        }

        // Select the property by clicking its "Book Now" or "Enquire" button
        WebElement selectedProperty = popularAccommodations.get(propertyIndex);
        WebElement bookOrEnquireButton = selectedProperty.findElement(bookOrEnquireButtonLocator);
        bookOrEnquireButton.click();

        System.out.println("Selected property at index: " + propertyIndex);
    }
    public void selectAutoSuggestion(By inputLocator,String inputText, By suggestionLocator) {
        WebElement inputField = driver.findElement(inputLocator);
        inputField.clear();
        inputField.sendKeys(inputText);

        // Wait for the auto-suggestions to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));

        // Click on the first suggestion (or you can add logic to select a specific one)
        List<WebElement> suggestions = driver.findElements(suggestionLocator);

        if (!suggestions.isEmpty()) {
            suggestions.get(0).click();  // Select the first suggestion
        } else {
            System.out.println("No suggestions found for the input: " + inputText);
        }
    }
    }

