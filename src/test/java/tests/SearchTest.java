package tests;

import base.BaseTest_LoggedOut;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.PropertyPage;
import pages.SafariNowHomePage;
import pages.AccomodationListPage;

@Test(groups = {"search"})
public class SearchTest extends BaseTest_LoggedOut {

    private SafariNowHomePage homePage;
    private AccomodationListPage accomodationListPage;
    private PropertyPage propertyPage;

    @BeforeClass(groups = {"search"})
    public void pageSetup() {
        this.homePage = new SafariNowHomePage(driver);
        this.accomodationListPage = new AccomodationListPage(driver);
        this.propertyPage = new PropertyPage(driver);
    }

    /**
     * This method runs before EACH @Test method in this class.
     * It ensures every test starts from a clean, known state (the homepage)
     * without duplicating code in the test methods.
     */
    @BeforeMethod(groups = {"search"})
    public void navigateToHomepage() {
        System.out.println("--- Resetting to homepage for new search test ---");
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleOverlays();
    }

    @DataProvider(name = "searchDestinations")
    public Object[][] searchDestinations() {
        return new Object[][]{
                {"Cape Town"},
                {"Pretoria"},
                {"Johannesburg"}
        };
    }

    @Test(dataProvider = "searchDestinations", description = "Verifies searching for a destination lands on the results list page.")
    public void testSearchForDestination(String destination) {
        // Act: The navigation/setup is now handled by @BeforeMethod
        homePage.performSearch(destination);

        // Assert
        String actualHeaderText = accomodationListPage.getPageHeaderText();
        Assert.assertTrue(
                actualHeaderText.contains(destination),
                STR."Search results page header should contain '\{destination}'. Actual was: '\{actualHeaderText}'"
        );
    }

    @Test(description = "Verifies searching for an exact property name lands directly on the property page.")
    public void testSearchForExactProperty() {
        String exactPropertyName = "Cape Town Lodge";

        // Act: The navigation/setup is now handled by @BeforeMethod
        homePage.performSearch(exactPropertyName);

        // Assert: This now correctly uses the new method from the refactored PropertyPage
        String actualPageTitle = propertyPage.getPageTitle();
        Assert.assertTrue(
                actualPageTitle.contains(exactPropertyName),
                STR."Page title should contain the exact property name '\{exactPropertyName}'. Actual was: '\{actualPageTitle}'"
        );
    }
}