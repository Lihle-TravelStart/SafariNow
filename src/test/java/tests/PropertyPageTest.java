// In: tests/PropertyPageTest.java
package tests;

import base.BaseTest; // <-- Crucial: Extend BaseTest for framework integration
import helpers.DatePickerHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AccomodationListPage;
import pages.PropertyPage;
import pages.SafariNowHomePage;
// Removed unused imports: DriverManager, SeleniumUtils, WaitUtils

@Test(groups = {"property"}) // Assign to a group for selective execution
public class PropertyPageTest extends BaseTest { // Inherit from BaseTest

    // Page Object instances (no longer WebDriver directly)
    private SafariNowHomePage homePage;
    private AccomodationListPage accomodationListPage;
    private PropertyPage propertyPage;
    private DatePickerHelper datePickerHelper; // Renamed 'help' for clarity

    @BeforeClass(groups = {"property"}) // Runs once before any test method in this class
    public void pageSetup() {
        // The 'driver' is inherited from BaseTest and is already initialized and logged in.
        // We just initialize our Page Objects with this shared driver.
        this.homePage = new SafariNowHomePage(driver);
        this.accomodationListPage = new AccomodationListPage(driver);
        this.propertyPage = new PropertyPage(driver);
        this.datePickerHelper = new DatePickerHelper(driver);

        // Ensure we are on the homepage for a clean start for these tests.
        // The BaseTest login might have navigated to RepoRefresh.
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleCookieBanner(); // Always handle potential overlays
    }

    /**
     * DataProvider for booking and quote request tests.
     * This allows testing with different destinations and dates easily.
     */
    @DataProvider(name = "propertyInteractionData")
    public Object[][] propertyInteractionData() {
        return new Object[][]{
                // destination, checkInMonth, checkInDay, checkOutMonth, checkOutDay, numberOfRooms, numberOfAdults
                {"Cape Town", "January 2025", "20", "January 2025", "25", "1 Room, 2 Adults"},
                // Add more test cases here, e.g., different dates, destinations, room configurations
                // {"Durban", "February 2025", "10", "February 2025", "15", "2 Rooms, 4 Adults"}
        };
    }

    /**
     * Tests the end-to-end flow for checking availability and initiating a booking.
     * This test is now data-driven and follows a logical user journey.
     */
    @Test(dataProvider = "propertyInteractionData")
    public void testCheckAvailabilityAndBookFlow(
            String destination,
            String checkInMonth,
            String checkInDay,
            String checkOutMonth,
            String checkOutDay,
            String roomConfiguration) {

        System.out.println(STR."Starting booking flow for: \{destination}, Check-in: \{checkInDay} \{checkInMonth}, Check-out: \{checkOutDay} \{checkOutMonth}");

        // Step 1: Search for a destination from the homepage
        homePage.enterDestination(destination);
        homePage.selectSuggestion(destination); // Use the refactored selectSuggestion
        homePage.clickSearchButton();

        // Step 2: Select the first property from the search results list
        accomodationListPage.selectFirstProperty();

        // Step 3: On the Property Page, input check-in and check-out dates
        // NOTE: You might need to click on the date input fields first if they are not active.
        // Example: propertyPage.clickCheckInDateInput(); // Add this method to PropertyPage if needed
        datePickerHelper.selectDateFromCalendar(checkInMonth, checkInDay);
        datePickerHelper.selectDateFromCalendar(checkOutMonth, checkOutDay);

        // Step 4: Select room configuration and check availability
        // Ensure propertyPage.selectRoom and propertyPage.clickCheckAvailability are correctly implemented
        propertyPage.selectRoom(roomConfiguration);
        propertyPage.clickCheckAvailability();

        // Step 5: Verify rooms are displayed after checking availability
        Assert.assertTrue(propertyPage.isRoomDisplayed(), "Rooms should be displayed after checking availability for " + destination + ".");

        // Step 6: Click 'Book Now' button
        propertyPage.clickBookNow();

        // Step 7: CRITICAL - Verify the booking success/next page
        // This requires a new Page Object for the booking/checkout page (e.g., BookingConfirmationPage)
        // Example:
        // BookingConfirmationPage bookingConfirmationPage = new BookingConfirmationPage(driver);
        // Assert.assertTrue(bookingConfirmationPage.isBookingSummaryDisplayed(), "Booking summary should be displayed.");
        // Assert.assertEquals(bookingConfirmationPage.getBookingDestination(), destination, "Booking destination mismatch.");
        System.out.println(STR."Successfully initiated booking for \{destination}. Further verification needed.");
    }

    /**
     * Tests the end-to-end flow for checking availability and requesting a quote.
     * This test follows a similar pattern to the booking flow.
     */
    @Test(dataProvider = "propertyInteractionData")
    public void testCheckAvailabilityAndRequestQuoteFlow(
            String destination,
            String checkInMonth,
            String checkInDay,
            String checkOutMonth,
            String checkOutDay,
            String roomConfiguration) {

        System.out.println(STR."Starting quote request flow for: \{destination}, Check-in: \{checkInDay} \{checkInMonth}, Check-out: \{checkOutDay} \{checkOutMonth}");

        // Step 1: Navigate and Search (same as booking flow)
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleCookieBanner();
        homePage.enterDestination(destination);
        homePage.selectSuggestion(destination);
        homePage.clickSearchButton();

        // Step 2: Select the first property from the search results list
        accomodationListPage.selectFirstProperty();

        // Step 3: On the Property Page, input check-in and check-out dates
        datePickerHelper.selectDateFromCalendar(checkInMonth, checkInDay);
        datePickerHelper.selectDateFromCalendar(checkOutMonth, checkOutDay);

        // Step 4: Select room configuration and check availability
        propertyPage.selectRoom(roomConfiguration);
        propertyPage.clickCheckAvailability();

        // Step 5: Verify rooms are displayed
        Assert.assertTrue(propertyPage.isRoomDisplayed(), "Rooms should be displayed after checking availability for " + destination + ".");

        // Step 6: Click 'Request Quote' button
        propertyPage.clickRequestQuote();

        // Step 7: CRITICAL - Verify the quote request confirmation/next page
        // This requires a new Page Object for the quote confirmation page (e.g., QuoteConfirmationPage)
        // Example:
        // QuoteConfirmationPage quoteConfirmationPage = new QuoteConfirmationPage(driver);
        // Assert.assertTrue(quoteConfirmationPage.isConfirmationMessageDisplayed(), "Quote confirmation message should be displayed.");
        System.out.println(STR."Successfully initiated quote request for \{destination}. Further verification needed.");
    }

    // No @AfterClass needed, as BaseTest handles the final driver.quit() for the entire suite.
}