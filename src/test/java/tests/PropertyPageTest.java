package tests;

import base.BaseTest_LoggedOut;
import helpers.DatePickerHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AccomodationListPage;
import pages.PropertyPage;
import pages.SafariNowHomePage;

@Test(groups = {"property"})
// CORRECTED: This test is for a logged-out user flow.
public class PropertyPageTest extends BaseTest_LoggedOut {

    private SafariNowHomePage homePage;
    private AccomodationListPage accomodationListPage;
    private PropertyPage propertyPage;
    private DatePickerHelper datePickerHelper;

    @BeforeClass(groups = {"property"})
    public void pageSetup() {
        this.homePage = new SafariNowHomePage(driver);
        this.accomodationListPage = new AccomodationListPage(driver);
        this.propertyPage = new PropertyPage(driver);
        this.datePickerHelper = new DatePickerHelper(driver);
    }

    @BeforeMethod(groups = {"property"})
    public void resetToHomePage() {
        System.out.println("--- Resetting state to homepage for new test ---");
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleOverlays();
    }

    @DataProvider(name = "propertyInteractionData")
    public Object[][] propertyInteractionData() {
        return new Object[][]{
                {"Cape Town", "January 2025", "20", "January 2025", "25", "1 Room, 2 Adults"},
        };
    }

    /**
     * REFACTORED: This private helper method contains all the common steps
     * for searching, selecting a property, and checking its availability.
     * This eliminates code duplication and makes the tests much cleaner.
     */
    private void performSearchAndSelectProperty(
            String destination,
            String checkInMonth, String checkInDay,
            String checkOutMonth, String checkOutDay,
            String roomConfiguration) {

        // Step 1: Search
        homePage.performSearch(destination);

        // Step 2: Select the first property from the search results list
        accomodationListPage.selectFirstProperty();

        // Step 3: On the Property Page, input check-in and check-out dates
        // TODO: Implement a robust DatePickerHelper
        // datePickerHelper.selectDateFromCalendar(checkInMonth, checkInDay);
        // datePickerHelper.selectDateFromCalendar(checkOutMonth, checkOutDay);

        // Step 4: Select room configuration and check availability
        propertyPage.selectRoom(roomConfiguration);
        propertyPage.clickCheckAvailability();

        // Step 5: Verify rooms are displayed
        Assert.assertTrue(propertyPage.isRoomDisplayed(), "Rooms should be displayed after checking availability for " + destination + ".");
    }

    @Test(dataProvider = "propertyInteractionData", description = "Tests the end-to-end flow for checking availability and initiating a booking.")
    public void testCheckAvailabilityAndBookFlow(
            String destination, String checkInMonth, String checkInDay,
            String checkOutMonth, String checkOutDay, String roomConfiguration) {

        System.out.println(STR."Starting booking flow for: \{destination}");

        // Call the helper to perform all common setup steps
        performSearchAndSelectProperty(destination, checkInMonth, checkInDay, checkOutMonth, checkOutDay, roomConfiguration);

        // Perform the unique final step for this test
        propertyPage.clickBookNow();

        // Final verification
        // TODO: Implement BookingConfirmationPage and add assertions here.
        System.out.println(STR."Successfully initiated booking for \{destination}. Further verification needed.");
    }

    @Test(dataProvider = "propertyInteractionData", description = "Tests the end-to-end flow for checking availability and requesting a quote.")
    public void testCheckAvailabilityAndRequestQuoteFlow(
            String destination, String checkInMonth, String checkInDay,
            String checkOutMonth, String checkOutDay, String roomConfiguration) {

        System.out.println(STR."Starting quote request flow for: \{destination}");

        // Call the helper to perform all common setup steps
        performSearchAndSelectProperty(destination, checkInMonth, checkInDay, checkOutMonth, checkOutDay, roomConfiguration);

        // Perform the unique final step for this test
        propertyPage.clickRequestQuote();

        // Final verification
        // TODO: Implement QuoteConfirmationPage and add assertions here.
        System.out.println(STR."Successfully initiated quote request for \{destination}. Further verification needed.");
    }
}