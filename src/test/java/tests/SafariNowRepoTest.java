package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.RepoPage;

@Test(groups = {"repo_actions"})
public class SafariNowRepoTest extends BaseTest {

    private RepoPage repoPage;

    @BeforeClass(groups = {"repo_actions"})
    public void pageSetup() {
        this.repoPage = new RepoPage(driver);
    }

    @BeforeMethod(groups = {"repo_actions"})
    public void navigateToRepoPage() {
        System.out.println("=== NAVIGATING TO REPO PAGE BEFORE TEST ===");
        repoPage.navigateToRepoPage();
        Assert.assertTrue(repoPage.isRefreshAvailabilityBtnDisplayed(), "Failed to navigate to or verify the RepoRefresh page.");
    }

    @Test(description = "Tests the 'Refresh Availability' button. The test passes if the action completes without error.")
    public void testRefreshAvailability() {
        System.out.println("=== Testing Refresh Availability ===");
        // The verification is now handled within the page object method.
        // If it fails, it will throw an exception, failing the test correctly.
        repoPage.clickRefreshAvailabilityBtn();
        System.out.println("✓ Refresh Availability action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Lead' button. The test passes if the action completes without error.")
    public void testRefreshLead() {
        System.out.println("=== Testing Refresh Lead ===");
        repoPage.clickRefreshLeadBtn();
        System.out.println("✓ Refresh Lead action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Listings' button. The test passes if the action completes without error.")
    public void testRefreshListings() {
        System.out.println("=== Testing Refresh Listings ===");
        repoPage.clickRefreshListingsBtn();
        System.out.println("✓ Refresh Listings action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Pricing' button. The test passes if the action completes without error.")
    public void testRefreshPricing() {
        System.out.println("=== Testing Refresh Pricing ===");
        repoPage.clickRefreshPricingBtn();
        System.out.println("✓ Refresh Pricing action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Seller' button. The test passes if the action completes without error.")
    public void testRefreshSeller() {
        System.out.println("=== Testing Refresh Seller ===");
        repoPage.clickRefreshSellerBtn();
        System.out.println("✓ Refresh Seller action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Seller Mongo' button. The test passes if the action completes without error.")
    public void testRefreshSellerMongo() {
        System.out.println("=== Testing Refresh Seller Mongo ===");
        repoPage.clickRefreshMongoBtn();
        System.out.println("✓ Refresh Seller Mongo action completed successfully.");
    }

    @Test(description = "Tests the 'Refresh Specials' button. The test passes if the action completes without error.")
    public void testRefreshSpecials() {
        System.out.println("=== Testing Refresh Specials ===");
        repoPage.clickRefreshSpecialsBtn();
        System.out.println("✓ Refresh Specials action completed successfully.");
    }
}