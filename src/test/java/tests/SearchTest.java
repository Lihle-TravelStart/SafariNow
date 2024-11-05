package tests;

import pages.SearchPage;
import utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest {
    private WebDriver driver;
    private SearchPage searchPage;

    @BeforeClass
    public void setup() {
        driver = DriverManager.getDriver(); // Initialize WebDriver
        driver.get("https://www.safarinow.com"); // Navigate to Safarinow
        searchPage = new SearchPage(driver); // Instantiate SearchPage
    }

    @Test
    public void testSearchAccommodation() {
        // Example data for the search
        String destination = "Cape Town";
        String checkInDate = "2024-10-20"; // Example date format
        String checkOutDate = "2024-10-27"; // Example date format

        // Perform search
       // searchPage.enterDestination(destination);
        searchPage.selectAutoSuggestion(searchPage.destinationInput,destination,searchPage.suggestionListLocator);
       // searchPage.clickSearchButton();

    }

    @AfterClass

    public void teardown() {
        DriverManager.quitDriver();
    }

}
