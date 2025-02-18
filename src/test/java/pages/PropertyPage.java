package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PropertyPage {

    private WebDriver driver;
    WebDriverWait wait;

    public By requestQoute = By.xpath("//a[@class='pointer btn-blue go-sticky-raq-button']");
    public By BookNow = By.xpath("//a[@class='seeinstantbook btn-green go-sticky-book-now-button']");
    public By propertyName = By.xpath("//h1[normalize-space()='TwentyFour 17 Inn']");
    public By fromPrice = By.xpath("//div[@data-bind='visible: !$root.hasDates() || !$root.hasAvailableRooms()']//div[@class='custom-lp-price-wrapper e-price']");
    public By propertyImage = By.cssSelector("div[class='carousel-item active'] img[alt='TwentyFour 17 Inn']");
    public By checkInField = By.xpath("//input[@id='dp1733828277904']");
    public By checkOutField = By.xpath("//input[@id='dp1733828277905']");
    public By roomsDropdown = By.id("rooms-dropdown");
    public By checkAvailabilityButton = By.xpath("//button[text()='Check Availability']");
    public By bookNowButton = By.xpath("//button[contains(@class, 'btn-green') and text()='Book Now']");
    public By requestQuoteButton = By.xpath("//button[contains(@class, 'btn-blue') and text()='Request Quote']");
    public By roomCard = By.cssSelector(".room-card"); // Assuming each room listing is within this element.
    public By roomNames = By.cssSelector(".room-name");
    public By roomImages = By.cssSelector(".room-image img");
    public By moreButton = By.xpath("//a[contains(text(), 'More')]");
    public By availabilityMessage = By.cssSelector(".availability-message");
    public PropertyPage(WebDriver driver) {
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



    public void inputCheckInDate(String date) {
        WebElement checkIn = driver.findElement(checkInField);
        checkIn.clear();
        checkIn.sendKeys(date);
    }

    public void inputCheckOutDate(String date) {
        WebElement checkOut = driver.findElement(checkOutField);
        checkOut.clear();
        checkOut.sendKeys(date);
    }

    public void selectRoom(String option) {
        WebElement rooms = driver.findElement(roomsDropdown);
        rooms.click();
        // Select option logic can be added here based on specific dropdown implementation.
    }

    public void clickCheckAvailability() {
        driver.findElement(checkAvailabilityButton).click();
    }

    public boolean isRoomDisplayed() {
        return !driver.findElements(roomCard).isEmpty();
    }

    public void clickBookNow() {
        driver.findElement(bookNowButton).click();
    }

    public void clickRequestQuote() {
        driver.findElement(requestQuoteButton).click();
    }

    public void enterCheckInDate(String date) {
        driver.findElement(checkInField).clear();
        driver.findElement(checkInField).sendKeys(date);
    }

    public void enterCheckOutDate(String date) {
        driver.findElement(checkOutField).clear();
        driver.findElement(checkOutField).sendKeys(date);
    }

    public List<WebElement> getRoomNames() {
        return driver.findElements(roomNames);
    }

    public List<WebElement> getRoomImages() {
        return driver.findElements(roomImages);
    }

    public List<WebElement> getMoreButtons() {
        return driver.findElements(moreButton);
    }

    public WebElement getAvailabilityMessage() {
        return driver.findElement(availabilityMessage);
    }

}


