package tests;

import pages.SafariNowHomePage;
import utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest {
    private WebDriver driver;
    private SafariNowHomePage homePage;

    @BeforeClass
    public void setup() {
        driver = DriverManager.getDriver(); // Initialize WebDriver
        driver.get("https://www.safarinow.com"); // Navigate to Safarinow
        homePage = new SafariNowHomePage(driver); // Instantiate SearchPage
    }

    @Test
    public void testSearchAccommodation() {
        // Example data for the search
        String destination = "Cape Town";

        // Perform search
        homePage.enterDestination(destination);
        homePage.selectDropdownOption(destination).click();

    }

    @AfterClass

    public void teardown() {
        DriverManager.quitDriver();
    }

}
