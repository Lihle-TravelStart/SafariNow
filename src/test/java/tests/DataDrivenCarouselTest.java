package tests;

import base.BaseTest_LoggedOut;
import data.TestDataLoader;
import data.TestDestination;
import data.TestScenario;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SafariNowHomePage;

import java.util.List;

@Test(groups = {"carousel", "data_driven"})
public class DataDrivenCarouselTest extends BaseTest_LoggedOut {

    private SafariNowHomePage homePage;
    private TestDataLoader testDataLoader;

    @BeforeClass(groups = {"carousel", "data_driven"})
    public void pageSetup() {
        this.homePage = new SafariNowHomePage(driver);
        // Use the singleton instance of the loader
        this.testDataLoader = TestDataLoader.getInstance();
    }

    @DataProvider(name = "destinationsProvider")
    public Object[][] getDestinations() {
        List<TestDestination> destinations = testDataLoader.getTestDestinations();
        Object[][] data = new Object[destinations.size()][1];
        for (int i = 0; i < destinations.size(); i++) {
            data[i][0] = destinations.get(i);
        }
        return data;
    }

    @DataProvider(name = "highPriorityDestinationsProvider")
    public Object[][] getHighPriorityDestinations() {
        List<TestDestination> destinations = testDataLoader.getDestinationsByPriority("high");
        Object[][] data = new Object[destinations.size()][1];
        for (int i = 0; i < destinations.size(); i++) {
            data[i][0] = destinations.get(i);
        }
        return data;
    }

    @DataProvider(name = "scenariosProvider")
    public Object[][] getTestScenarios() {
        List<TestScenario> scenarios = testDataLoader.getTestScenarios();
        Object[][] data = new Object[scenarios.size()][1];
        for (int i = 0; i < scenarios.size(); i++) {
            data[i][0] = scenarios.get(i);
        }
        return data;
    }

    @Test(dataProvider = "destinationsProvider", description = "Verifies that searching for any destination from the data file works.")
    public void testSearchForAllDestinations(TestDestination destination) {
        System.out.println(String.format("--- Testing Search for: %s ---", destination.getName()));
        homePage.navigateToUrl(testDataLoader.getEnvironmentUrl("production"));
        homePage.handleOverlays();
        homePage.performSearch(destination.getName());
        // A simple assertion to verify the search results page is loaded
        Assert.assertTrue(driver.getTitle().contains(destination.getName()),
                "Page title should contain the searched destination name.");
    }
}