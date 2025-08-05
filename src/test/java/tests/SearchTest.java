// In: tests/SearchTest.java
package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SafariNowHomePage;
import pages.SearchResultsPage;

@Test(groups = {"search"})
public class SearchTest extends BaseTest {

    private SafariNowHomePage homePage;
    private SearchResultsPage searchResultsPage;

    @BeforeClass(groups = {"search"})
    public void pageSetup() {
        this.homePage = new SafariNowHomePage(driver);
        this.searchResultsPage = new SearchResultsPage(driver);
    }

    @DataProvider(name = "searchDestinations")
    public Object[][] searchDestinations() {
        return new Object[][]{
                {"Cape Town"},
                {"Durban"},
                {"Johannesburg"},
                {"Garden Route"}
        };
    }

    /**
     * This single, data-driven test verifies the end-to-end search flow.
     * It now uses a high-level method from the page object, making it very clean.
     */
    @Test(dataProvider = "searchDestinations")
    public void testSearchAccommodation(String destination) {
        // Arrange: Navigate to the homepage for a clean state.
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleCookieBanner();

        // Act: Perform the entire search flow with a single, readable command.
        homePage.performSearch(destination);

        // Assert: Verify the outcome on the results page.
        String actualHeaderText = searchResultsPage.getPageHeaderText();
        Assert.assertTrue(
                actualHeaderText.contains(destination),
                STR."Search results page header should contain '\{destination}'. Actual was: '\{actualHeaderText}'"
        );
    }
}