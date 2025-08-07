package base;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import pages.LoginPage;
import pages.RepoPage;
import pages.SafariNowHomePage;

import java.time.Duration;
import java.util.Base64;

/**
 * Manages the test suite lifecycle for LOGGED-IN tests.
 * It now gets the driver from the central WebDriverLifecycleManager.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void loggedInSetup() {
        System.out.println("=== STARTING LOGGED-IN CLASS SETUP ===");
        this.driver = WebDriverLifecycleManager.getDriver();

        try {
            performLoginAndVerification();
            System.out.println("✓ Logged-in class setup completed successfully");
        } catch (Exception e) {
            System.err.println("✗ Critical setup failure: " + e.getMessage());
            Assert.fail(STR."Critical setup failure during login for this class: \{e.getMessage()}", e);
        }
    }

    /**
     * FIX: This method is now idempotent. It first checks if a login is
     * necessary before attempting the login flow.
     */
    private void performLoginAndVerification() {
        // First, check if we are already logged in.
        if (isUserLoggedIn()) {
            System.out.println("✓ User is already logged in. Skipping login sequence.");
            return; // Exit the method early
        }

        // If not logged in, proceed with the full login sequence.
        System.out.println("User is not logged in. Starting login sequence...");
        SafariNowHomePage homePage = new SafariNowHomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.navigateToUrl("https://www.safarinow.com");
        homePage.handleOverlays();
        homePage.clickMenuAccount();
        homePage.clickLoginLink();

        String password = getDecodedPassword();
        loginPage.login("lihle@safarinow.com", password);

        waitForLoginCompletion();

        System.out.println("Navigating to the staff RepoRefresh page...");
        driver.get("https://www.safarinow.com/staff/RepoRefresh");

        verifyRepoPageLoaded();
        System.out.println("✓ Login successful and correctly navigated to RepoRefresh page. Proceeding with tests.");
    }

    /**
     * A helper method to safely check for the presence of an element
     * that only appears when a user is logged in.
     * @return true if the user is logged in, false otherwise.
     */
    private boolean isUserLoggedIn() {
        try {
            // Use a very short wait to avoid slowing down the tests.
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            // This is the same reliable locator we use to confirm login.
            By userNameLocator = By.xpath("//*[@id='MenuAccount' and contains(., 'Lihle')]");
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(userNameLocator));
            return true; // If the element is found, the user is logged in.
        } catch (TimeoutException e) {
            return false; // If the element is not found after 2 seconds, the user is not logged in.
        }
    }

    private void waitForLoginCompletion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            By userNameLocator = By.xpath("//*[@id='MenuAccount' and contains(., 'Lihle')]");
            System.out.println("Waiting for user's name to appear to confirm login session is active...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(userNameLocator));
            System.out.println("✓ User's name is visible. Login state confirmed.");
        } catch (Exception e) {
            throw new RuntimeException("Login may have failed; the user's name did not become visible after login attempt.", e);
        }
    }

    private void verifyRepoPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.titleContains("Repo Refresh"));
            System.out.println("✓ Page title 'Repo Refresh' confirmed.");
            RepoPage repoPage = new RepoPage(driver);
            Assert.assertTrue(repoPage.isRefreshAvailabilityBtnDisplayed(), "RepoRefresh page loaded, but 'Refresh Availability' button was not found.");
            System.out.println("✓ 'Refresh Availability' button found.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to verify that the RepoRefresh page loaded correctly.", e);
        }
    }

    private String getDecodedPassword() {
        String encodedPassword = System.getenv("SAFARINOW_PASSWORD");
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            throw new IllegalStateException("SAFARINOW_PASSWORD environment variable is not set.");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        return new String(decodedBytes);
    }
}