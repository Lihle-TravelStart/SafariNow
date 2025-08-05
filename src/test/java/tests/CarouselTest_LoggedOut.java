// Create new file: tests/CarouselTest_LoggedOut.java
package tests;

import helpers.CarouselHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SafariNowHomePage;

import java.time.Duration;

/**
 * Verifies carousel functionality for a LOGGED-OUT (public) user.
 * This class manages its own WebDriver instance to guarantee a clean session.
 */
@Test(groups = {"carousel", "logged_out"})
public class CarouselTest_LoggedOut {

    private WebDriver driver;
    private CarouselHelper carouselHelper;
    private SafariNowHomePage safariNowHomePage;

    @BeforeClass
    public void setup() {
        // This test manages its own, separate browser session.
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        this.carouselHelper = new CarouselHelper(driver);
        this.safariNowHomePage = new SafariNowHomePage(driver);

        // Navigate to the page and handle overlays
        safariNowHomePage.navigateToUrl("https://www.safarinow.com/");
        safariNowHomePage.handleCookieBanner();
    }

    @DataProvider(name = "carouselDataProvider")
    public Object[][] carouselDataProvider() {
        // Define the carousels expected for a logged-out user
        return new Object[][]{
                {"Deals and Offers"},
                {"Popular Destinations"},
                {"Weekend Getaways"},
                {"City Trips"},
                {"Kwazulu-Natal Beach Escapes"},
                {"Wildlife Adventures"},
                {"Explore the Garden Route"},
                {"Amazing Country Escapes"},
                {"West Coast Getaways"},
                {"Discover the Winelands"},
                {"Incredible Road Trips"},
                {"Stop Overs"}
        };
    }

    @Test(dataProvider = "carouselDataProvider")
    public void testCarouselFunctionality(String carouselName) {
        System.out.println(STR."=== TESTING LOGGED-OUT CAROUSEL: \{carouselName} ===");
        performCarouselChecks(carouselName);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Reusable helper method to perform the actual carousel checks.
     */
    private void performCarouselChecks(String carouselName) {
        // 1. Verify presence
        boolean isPresent = safariNowHomePage.isCarouselPresent(carouselName);
        Assert.assertTrue(isPresent, STR."Carousel '\{carouselName}' should be present but was not found.");

        // 2. Generate locators
        By container = safariNowHomePage.getCarouselContainerByLabel(carouselName);
        By nextButton = safariNowHomePage.getCarouselNextButtonByLabel(carouselName);
        By prevButton = safariNowHomePage.getCarouselPrevButtonByLabel(carouselName);
        By items = safariNowHomePage.getCarouselItemsByLabel(carouselName);

        // 3. Perform functional assertions
        Assert.assertTrue(carouselHelper.isCarouselPresent(container, items),
                STR."\{carouselName} carousel container and items should be visible.");

        Assert.assertTrue(carouselHelper.areImagesChanging(nextButton, items),
                STR."\{carouselName} images should change when clicking next button.");

        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(prevButton, items),
                STR."\{carouselName} images should change when clicking previous button.");

        System.out.println(STR."✓ \{carouselName} test completed successfully");
    }
}