package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SafariNowHomePage {
    private WebDriver driver;
    WebDriverWait wait;

    public By checkInField = By.xpath("//input[@id='Checkin']");
    public By checkOutField = By.xpath("//input[@id='Checkout']");
    // Locators
    private By loginButton = By.xpath("//*[@id='MenuItems']/ul/li[3]/a");
    private By menuButton = By.xpath("//*[@id='MenuAccount']/a/span");
    public By destinationInput = By.id("SearchFilterFilterSearchTerm"); // Replace with actual locator
    private By checkInDateInput = By.id("check-in"); // Replace with actual locator
    private By checkOutDateInput = By.id("check-out"); // Replace with actual locator
    private By searchButton = By.id("btnSearch"); // Replace with actual locator
    public By suggestionListLocator = By.xpath("//*[@id='scrollable-dropdown-menu']/span/div/div/div[2]/div/div[1]");
    private By suggestionItemsLocator = By.xpath("//ul[contains(@class, 'suggestions-list')]/li");
    private By popularAccommodationLocator = By.cssSelector(".popular-accommodations .accommodation-item");
    private By bookOrEnquireButtonLocator = By.xpath(".//button[contains(text(), 'Book Now') or contains(text(), 'Enquire')]");
    private By suggestResults = By.xpath("//div[normalize-space()='" + "Cape Town" + "']");
                                                 //*[@id="scrollable-dropdown-menu"]/span/div/div/div[2]/div/div[1]

    public SafariNowHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterDestination(String destination) {
        WebElement destinationField = driver.findElement(destinationInput);
        destinationField.clear();
        destinationField.sendKeys(destination);
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
    public WebElement selectDropdownOption(String optionText) {
        String xpath = "//*[@id=\"scrollable-dropdown-menu\"]/span/div/div/div[2]/div/div[1]";
        return driver.findElement(By.xpath(xpath));
    }

    //*[@id="scrollable-dropdown-menu"]/span/div/div/div[3]/div/div[1]

    public void selectAutoSuggestion(By inputLocator,String inputText) {
        WebElement inputField = driver.findElement(inputLocator);
        inputField.clear();
        inputField.sendKeys(inputText);

        // Wait for the auto-suggestions to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestResults));

        // Click on the first suggestion (or you can add logic to select a specific one)
        List<WebElement> suggestions = driver.findElements(suggestResults);

        if (!suggestions.isEmpty()) {
            suggestions.get(0).click();  // Select the first suggestion
        } else {
            System.out.println("No suggestions found for the input: " + inputText);
        }
    }
    public void clickLoginButton() {
        WebElement loginBtn = driver.findElement(loginButton);
        loginBtn.click();
    }
    public void clickmenuButton() {
        WebElement menuBtn = driver.findElement(menuButton);
        menuBtn.click();
    }
    public void navigateToOtherURL(String url) {
        driver.navigate().to(url);
    }
    }

