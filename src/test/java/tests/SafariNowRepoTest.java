package tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.AccomodationListPage;
import pages.LoginPage;
import pages.RepoPage;
import pages.SafariNowHomePage;
import utils.SeleniumUtils;
//import pages.repoPage;

import java.time.Duration;

public class SafariNowRepoTest {
    private WebDriver driver;
    private SafariNowHomePage homePage;
    private LoginPage loginPage;
    private RepoPage repoPage;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.safarinow.com");
        homePage = new SafariNowHomePage(driver);
        loginPage = new LoginPage(driver);
        repoPage = new RepoPage(driver);
    }
    @BeforeClass
    public void testLoginAndRepoNavigation() {
        homePage.clickmenuButton();
        homePage.clickLoginButton();
        loginPage.login("lihle@safarinow.com","4yF6:Zz)[t5\\");
        homePage.navigateToOtherURL("https://www.safarinow.com/staff/RepoRefresh");
    }
    @Test
    public void testRefreshAvailibity() {
        repoPage.clickRefreshAvailabilityBtn();
       // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshLead() {
        repoPage.clickRefreshLeadBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshListings() {
        repoPage.clickRefreshListingsBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshPricing() {
        repoPage.clickRefreshPricingBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshSeller() {
        repoPage.clickRefreshSellerBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshSellerMongo() {
        repoPage.clickRefreshMongoBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @Test
    public void testRefreshSpecials() {
        repoPage.clickRefreshSpecialsBtn();
        // repoPage.isPopupDisplayed();
        boolean isPopupDisplayed = repoPage.isPopupDisplayed();
        // Assert that the popup was displayed, test should fail if false is returned
        Assert.assertTrue(isPopupDisplayed, "Popup was not displayed");

    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
