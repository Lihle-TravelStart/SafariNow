package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RepoPage;
import pages.SafariNowHomePage;

import java.time.Duration;

@Test(groups = {"repo"})
public class SafariNowRepoTest {
    private static boolean loggedIn = false;
    private static WebDriver driver;
    private SafariNowHomePage homePage;
    private LoginPage loginPage;
    private RepoPage repoPage;

    private static void loginAndNavigate() {
        driver.get("https://www.safarinow.com");
        SafariNowHomePage staticHomePage = new SafariNowHomePage(driver);
        LoginPage staticLoginPage = new LoginPage(driver);
        staticHomePage.clickmenuButton();
        staticHomePage.clickLoginButton();
        staticLoginPage.login("lihle@safarinow.com", "4yF6:Zz)[t5\\");
        staticHomePage.navigateToOtherURL("https://www.safarinow.com/staff/RepoRefresh");
    }

    @BeforeClass(groups = {"repo"})
    public void setUp() {
        if (driver == null) {
            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().window().maximize();
        }
        if (!loggedIn) {
            loginAndNavigate();
            loggedIn = true;
        }
        homePage = new SafariNowHomePage(driver);
        loginPage = new LoginPage(driver);
        repoPage = new RepoPage(driver);
    }

    @Test
    public void testRefreshAvailability() {
        repoPage.clickRefreshAvailabilityBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshLead() {
        repoPage.clickRefreshLeadBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshListings() {
        repoPage.clickRefreshListingsBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshPricing() {
        repoPage.clickRefreshPricingBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshSeller() {
        repoPage.clickRefreshSellerBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshSellerMongo() {
        repoPage.clickRefreshMongoBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @Test
    public void testRefreshSpecials() {
        repoPage.clickRefreshSpecialsBtn();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
            loggedIn = false;
        }
    }
}