package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.RepoPage;

@Test(groups = {"repo"})
public class SafariNowRepoTest extends BaseTest {

    private RepoPage repoPage;

    @BeforeClass(groups = {"repo"})
    public void pageSetup() {
        this.repoPage = new RepoPage(driver);
    }

    @BeforeMethod
    public void cleanupBeforeTest() {
        System.out.println("=== CLEANING UP BEFORE TEST ===");
        // Ensure no alerts are present before starting each test
        repoPage.ensureNoAlertsPresent();
        System.out.println("=== CLEANUP COMPLETE ===");
    }

    @Test
    public void testRefreshAvailability() {
        System.out.println("=== Testing Refresh Availability ===");

        // Click the button
        repoPage.clickRefreshAvailabilityBtn();

        // Verify alert appears
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Availability");

        // Accept the alert to continue
        repoPage.acceptAlert();

        System.out.println("✓ Refresh Availability test completed successfully");
    }

    @Test
    public void testRefreshLead() {
        System.out.println("=== Testing Refresh Lead ===");

        repoPage.clickRefreshLeadBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Lead");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Lead test completed successfully");
    }

    @Test
    public void testRefreshListings() {
        System.out.println("=== Testing Refresh Listings ===");

        repoPage.clickRefreshListingsBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Listings");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Listings test completed successfully");
    }

    @Test
    public void testRefreshPricing() {
        System.out.println("=== Testing Refresh Pricing ===");

        repoPage.clickRefreshPricingBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Pricing");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Pricing test completed successfully");
    }

    @Test
    public void testRefreshSeller() {
        System.out.println("=== Testing Refresh Seller ===");

        repoPage.clickRefreshSellerBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Seller");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Seller test completed successfully");
    }

    @Test
    public void testRefreshSellerMongo() {
        System.out.println("=== Testing Refresh Seller Mongo ===");

        repoPage.clickRefreshMongoBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Seller Mongo");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Seller Mongo test completed successfully");
    }

    @Test
    public void testRefreshSpecials() {
        System.out.println("=== Testing Refresh Specials ===");

        repoPage.clickRefreshSpecialsBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        Assert.assertTrue(isPopupDisplayed, "Alert was not displayed after clicking Refresh Specials");

        repoPage.acceptAlert();

        System.out.println("✓ Refresh Specials test completed successfully");
    }
}
