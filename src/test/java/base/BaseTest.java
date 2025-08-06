package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pages.LoginPage;
import pages.RepoPage;
import pages.SafariNowHomePage;

import java.time.Duration;
import java.util.Base64;

/**
 * Manages the test suite lifecycle for LOGGED-IN tests.
 * It now uses the WebDriverLifecycleManager for robust cleanup.
 */
public abstract class BaseTest {

    protected static WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        System.out.println("=== STARTING SUITE SETUP ===");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(configureChromeOptions());
        // Register the driver with the lifecycle manager
        WebDriverLifecycleManager.register(driver);

        setupWindowSize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("✓ WebDriver setup complete");

        try {
            performLoginAndVerification();
            System.out.println("✓ Suite setup completed successfully");
        } catch (Exception e) {
            System.err.println("✗ Critical setup failure: " + e.getMessage());
            // Use the manager to clean up
            WebDriverLifecycleManager.quitDriver(driver);
            Assert.fail(STR."Critical setup failure during login, cannot continue tests: \{e.getMessage()}", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        // Delegate cleanup to the lifecycle manager
        WebDriverLifecycleManager.quitDriver(driver);
    }

    private void performLoginAndVerification() {
        SafariNowHomePage homePage = new SafariNowHomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.navigateToUrl("https://www.safarinow.com");
        // CORRECTED: Call the new, more robust handleOverlays() method.
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

    private void waitForLoginCompletion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // --- THE FIX: This locator is more robust and less brittle ---
            // It finds the account menu element and verifies the text 'Lihle' exists anywhere inside it.
            By userNameLocator = By.xpath("//*[@id='MenuAccount' and contains(., 'Lihle')]");
            // --- END FIX ---
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

    private ChromeOptions configureChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadlessEnvironment()) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
        }
        options.addArguments("--disable-web-security", "--disable-features=VizDisplayCompositor", "--remote-debugging-port=9222", "--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return options;
    }

    private void setupWindowSize() {
        try {
            if (!isHeadlessEnvironment()) {
                driver.manage().window().maximize();
                System.out.println("✓ Window maximized");
            }
        } catch (WebDriverException e) {
            System.out.println(STR."Window maximize failed, using manual sizing: \{e.getMessage()}");
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    private boolean isHeadlessEnvironment() {
        return System.getenv("CI") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                Boolean.parseBoolean(System.getProperty("java.awt.headless", "false"));
    }
}