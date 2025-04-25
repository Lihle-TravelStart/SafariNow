package tests;

import helpers.DatePickerHelper;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.AccomodationListPage;
import pages.PropertyPage;
import pages.SafariNowHomePage;
import utils.DriverManager;
import utils.SeleniumUtils;
import utils.WaitUtils;

public class PropertyPageTest {
    // Instantiate utilities
    private WebDriver driver;
    private PropertyPage propertyPage;
    private SeleniumUtils seleniumUtils;
    private WaitUtils waitUtils;
    private SafariNowHomePage homePage;
    private AccomodationListPage accomodationListPage;
    private DatePickerHelper help;
    @BeforeClass
    public void setup() {
        driver = DriverManager.getDriver(); // Initialize WebDriver
        driver.get("https://www.safarinow.com"); // Navigate to Safarinow
        propertyPage = new PropertyPage(driver);
        homePage = new SafariNowHomePage(driver);
        accomodationListPage = new AccomodationListPage(driver);
        help = new DatePickerHelper(driver);
    }

    @Test
    public void testCheckAvailabilityAndBook() {

        String destination = "Cape Town";
        homePage.enterDestination(destination);
        homePage.selectDropdownOption(destination).click();
        SeleniumUtils.clickElement(AccomodationListPage.checkInDateInput);
        help.selectDateFromCalendar("January 2025", "20");
        //accomodationListPage.enterCheckInDate("2024-12-01");
       // accomodationListPage.enterCheckOutDate("2024-12-03");
       // propertyPage.selectRoom("1 Room, 2 Adults");
       // propertyPage.clickCheckAvailability();

        // Step 2: Verify rooms are displayed
        Assert.assertTrue(propertyPage.isRoomDisplayed(), "Rooms are not displayed!");

        // Step 3: Click 'Book Now' button
        propertyPage.clickBookNow();
        // Add verification for booking success page.
    }

    @Test
    public void testCheckAvailabilityAndRequestQuote() {
        // Step 1: Input check-in and check-out dates
        propertyPage.inputCheckInDate("2024-12-01");
        propertyPage.inputCheckOutDate("2024-12-03");
        propertyPage.selectRoom("1 Room, 2 Adults");
        propertyPage.clickCheckAvailability();

        // Step 2: Verify rooms are displayed
        Assert.assertTrue(propertyPage.isRoomDisplayed(), "Rooms are not displayed!");

        // Step 3: Click 'Request Quote' button
        propertyPage.clickRequestQuote();
        // Add verification for quote request confirmation.
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
