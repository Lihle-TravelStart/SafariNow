package tests;

import base.BaseTest_LoggedOut;
import helpers.ImageHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AccomodationListPage;
import pages.SafariNowHomePage;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Test(groups = {"search_results"})
public class SearchResultsTest extends BaseTest_LoggedOut {

    private SafariNowHomePage homePage;
    private AccomodationListPage accomodationListPage;
    private ImageHelper imageHelper;

    @BeforeClass(groups = {"search_results"})
    public void searchSetup() {
        this.homePage = new SafariNowHomePage(driver);
        this.accomodationListPage = new AccomodationListPage(driver);
        this.imageHelper = new ImageHelper(driver);

        System.out.println("--- Setting up for Search Results Test by searching for 'Cape Town' ---");
        homePage.navigateToUrl("https://www.safarinow.com/");
        homePage.handleOverlays();
        homePage.performSearch("Cape Town");
        Assert.assertTrue(accomodationListPage.getPageHeaderText().contains("Cape Town"),
                "Setup failed: Did not land on the Cape Town search results page.");
        System.out.println("✓ Successfully on search results page. Ready to test content.");
    }

    @Test(description = "Verifies that the first few property cards have a name, a loaded image, and a price if available.")
    public void testPropertyCardsHaveContent() {
        List<WebElement> cards = accomodationListPage.getPropertyCards();
        int limit = Math.min(cards.size(), 5);
        Assert.assertTrue(limit > 0, "Cannot test card content because no cards were found.");

        for (int i = 0; i < limit; i++) {
            WebElement card = cards.get(i);
            String propertyName = accomodationListPage.getPropertyName(card);

            List<WebElement> images = accomodationListPage.findPropertyImages(card);
            Assert.assertFalse(images.isEmpty(), STR."Property card '\{propertyName}' should have an image element.");
            Assert.assertTrue(imageHelper.isImageLoaded(images.get(0)),
                    STR."Image for property '\{propertyName}' should be loaded correctly.");

            Optional<WebElement> price = accomodationListPage.findDiscountedPrice(card);
            if (price.isPresent()) {
                String propertyPrice = price.get().getText();
                Assert.assertFalse(propertyPrice.trim().isEmpty(), STR."Property card '\{propertyName}' price should not be empty.");
                System.out.println(STR."✓ Verified content for: \{propertyName} (Price: \{propertyPrice})");
            } else {
                System.out.println(STR."✓ Verified content for: \{propertyName} (No price listed - this is OK)");
            }
        }
    }

    @DataProvider(name = "filtersToTest")
    public Object[][] filtersToTest() {
        // "Special Deals Only" is removed as it has its own, more complex test.
        return new Object[][]{
                {"Swimming Pool"}
        };
    }

    @Test(dataProvider = "filtersToTest", description = "Verifies that applying a filter reduces the total number of search results.")
    public void testFilterReducesTotalResultsCount(String filterToTest) {
        System.out.println(STR."--- Testing Filter: \{filterToTest} ---");
        driver.navigate().refresh();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        System.out.println("✓ Page refreshed, starting from a clean state.");

        int initialCount = accomodationListPage.getTotalResultsCount();
        Assert.assertTrue(initialCount > 0, "Cannot test filters without an initial list of results.");
        System.out.println(STR."Initial total result count: \{initialCount}");

        accomodationListPage.applyFilterByLabel(filterToTest);

        // Robustly wait for the total results count to change, indicating the filter has been applied.
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                d -> accomodationListPage.getTotalResultsCount() != initialCount
        );
        System.out.println("✓ Detected that the total results count has updated.");

        int filteredCount = accomodationListPage.getTotalResultsCount();
        System.out.println(STR."Filtered total result count: \{filteredCount}");

        Assert.assertTrue(filteredCount < initialCount,
                STR."Applying filter '\{filterToTest}' should reduce the total number of results. Initial count was \{initialCount}, but filtered count was \{filteredCount}.");
    }

    @Test(description = "Verifies the pricing logic for properties filtered by 'Special Deals Only'")
    public void testSpecialDealsFilterShowsCorrectPricing() {
        System.out.println("--- Testing Filter: Special Deals Only ---");
        driver.navigate().refresh();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));

        int initialCount = accomodationListPage.getTotalResultsCount();
        accomodationListPage.applyFilterByLabel("Special Deals Only");

        // Wait for the results to update
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                d -> accomodationListPage.getTotalResultsCount() != initialCount
        );

        List<WebElement> cards = accomodationListPage.getPropertyCards();
        Assert.assertFalse(cards.isEmpty(), "Filtering by 'Special Deals' should still yield results.");

        for (WebElement card : cards) {
            String propertyName = accomodationListPage.getPropertyName(card);
            System.out.println(STR."Verifying special deal logic for: \{propertyName}");

            Optional<WebElement> originalPriceEl = accomodationListPage.findOriginalPrice(card);
            Optional<WebElement> discountedPriceEl = accomodationListPage.findDiscountedPrice(card);
            Optional<WebElement> percentageEl = accomodationListPage.findDiscountPercentage(card);

            // Assert that all parts of a special deal are present
            Assert.assertTrue(originalPriceEl.isPresent(), STR."'\{propertyName}' should have an original price.");
            Assert.assertTrue(discountedPriceEl.isPresent(), STR."'\{propertyName}' should have a discounted price.");
            Assert.assertTrue(percentageEl.isPresent(), STR."'\{propertyName}' should have a discount percentage.");

            // Parse values, removing currency and symbols
            double originalPrice = Double.parseDouble(originalPriceEl.get().getText().replaceAll("[^\\d.]", ""));
            double discountedPrice = Double.parseDouble(discountedPriceEl.get().getText().replaceAll("[^\\d.]", ""));
            double percentage = Double.parseDouble(percentageEl.get().getText().replaceAll("[^\\d.]", ""));

            // 1. Assert that the discount is real
            Assert.assertTrue(discountedPrice < originalPrice,
                    STR."Discounted price (\{discountedPrice}) should be less than original price (\{originalPrice}) for '\{propertyName}'.");

            // 2. Assert that the percentage calculation is correct (within a small tolerance for rounding)
            double calculatedPrice = originalPrice * (1 - (percentage / 100.0));
            Assert.assertEquals(discountedPrice, calculatedPrice, 1.0,
                    STR."Calculated price did not match displayed price for '\{propertyName}'. Expected ~\{String.format("%.2f", calculatedPrice)} but was \{discountedPrice}.");

            System.out.println(STR."✓ Correctly verified special deal for '\{propertyName}'");
        }
    }
}