package tests;

import base.BaseTest_LoggedOut;
import helpers.CarouselHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SafariNowHomePage;

import java.time.Duration;

/**
 * Verifies carousel functionality for a LOGGED-OUT (public) user.
 * This class now extends BaseTest_LoggedOut for standardized, robust browser management.
 */
@Test(groups = {"carousel", "logged_out"})
public class CarouselTest_LoggedOut extends BaseTest_LoggedOut {

    private CarouselHelper carouselHelper;
    private SafariNowHomePage safariNowHomePage;

    /**
     * Initializes Page Objects and navigates to the homepage once per class.
     */
    @BeforeClass
    public void pageSetup() {
        // The 'driver' is inherited from BaseTest_LoggedOut.
        // We just initialize our Page Objects and Helpers.
        this.carouselHelper = new CarouselHelper(driver);
        this.safariNowHomePage = new SafariNowHomePage(driver);

        // Navigate to the page and handle any pop-up overlays.
        safariNowHomePage.navigateToUrl("https://www.safarinow.com/");
        // CORRECTED: Call the new, more robust handleOverlays() method.
        safariNowHomePage.handleOverlays();
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

    /**
     * IMPROVED: Reusable helper method to perform the actual carousel checks.
     * This method now robustly handles lazy-loaded content by scrolling
     * the carousel title into view before performing any checks.
     */
    private void performCarouselChecks(String carouselName) {
        // Step 1: Find the carousel's title element. This is our anchor.
        By titleLocator = By.xpath(String.format("//h3[normalize-space()='%s']", carouselName));
        WebElement titleElement;
        try {
            titleElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(titleLocator));
        } catch (TimeoutException e) {
            Assert.fail(STR."Could not find the title for carousel '\{carouselName}'. The carousel may not exist on the page.", e);
            return;
        }

        // Step 2: CRITICAL - Scroll the title into view to trigger lazy-loading.
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", titleElement);
        System.out.println(STR."✓ Scrolled '\{carouselName}' into view.");

        // Step 3: NOW, verify the carousel is present.
        boolean isPresent = safariNowHomePage.isCarouselPresent(carouselName);
        Assert.assertTrue(isPresent, STR."Carousel '\{carouselName}' should be present but was not found AFTER scrolling into view.");

        // Step 4: Generate locators for the now-visible elements
        By container = safariNowHomePage.getCarouselContainerByLabel(carouselName);
        By nextButton = safariNowHomePage.getCarouselNextButtonByLabel(carouselName);
        By prevButton = safariNowHomePage.getCarouselPrevButtonByLabel(carouselName);
        By items = safariNowHomePage.getCarouselItemsByLabel(carouselName);

        // Step 5: Perform functional assertions
        Assert.assertTrue(carouselHelper.isCarouselPresent(container, items),
                STR."\{carouselName} carousel container and items should be visible.");

        Assert.assertTrue(carouselHelper.areImagesChanging(nextButton, items),
                STR."\{carouselName} images should change when clicking next button.");

        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(prevButton, items),
                STR."\{carouselName} images should change when clicking previous button.");

        System.out.println(STR."✓ \{carouselName} test completed successfully");
    }
}