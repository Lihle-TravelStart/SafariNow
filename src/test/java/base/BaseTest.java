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
 * BaseTest with enhanced server error handling and alternative navigation strategies.
 */
public abstract class BaseTest {

    protected static WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        System.out.println("=== STARTING SUITE SETUP ===");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(configureChromeOptions());
        setupWindowSize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("✓ WebDriver setup complete");

        try {
            performLoginAndVerification();
            System.out.println("✓ Suite setup completed successfully");
        } catch (Exception e) {
            System.err.println("✗ Critical setup failure: " + e.getMessage());
            e.printStackTrace();
            captureDebugInfo();
            suiteTeardown();
            Assert.fail(STR."Critical setup failure during login, cannot continue tests: \{e.getMessage()}", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        if (driver != null) {
            try {
                System.out.println("Cleaning up WebDriver...");
                driver.quit();
                System.out.println("✓ WebDriver cleanup complete");
            } catch (Exception e) {
                System.err.println(STR."Error during WebDriver quit: \{e.getMessage()}");
            } finally {
                driver = null;
            }
        }
    }


    private void performLoginAndVerification() {
        SafariNowHomePage homePage = new SafariNowHomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        // Steps 1-5: Standard login process
        System.out.println("Step 1: Navigating to SafariNow homepage...");
        homePage.navigateToUrl("https://www.safarinow.com");

        System.out.println("Step 2: Handling cookie banner...");
        homePage.handleCookieBanner();

        System.out.println("Step 3: Clicking menu account...");
        homePage.clickMenuAccount();

        System.out.println("Step 4: Clicking login link...");
        homePage.clickLoginLink();

        System.out.println("Step 5: Performing login...");
        String password = getDecodedPassword();
        loginPage.login("lihle@safarinow.com", password);

        // Step 6: Wait for the server to process the login.
        waitForLoginCompletion();

        // Step 7: Direct navigation to the staff page.
        System.out.println("Step 7: Navigating directly to RepoRefresh page...");
        // We use the full .aspx URL which has proven to be reliable.
        driver.get("https://www.safarinow.com/staff/RepoRefresh");

        // Step 8: Verify that we have landed on the correct page.
        verifyRepoPageLoaded();

        System.out.println("✓ Login successful and correctly navigated to RepoRefresh page. Proceeding with tests.");
    }

    /**
     * Enhanced navigation method that tries multiple strategies to reach the RepoRefresh page.
     */
    private boolean attemptRepoPageNavigation(RepoPage repoPage) {
        String[] urlsToTry = {
                "https://www.safarinow.com/staff/RepoRefresh",
                "https://www.safarinow.com/Staff/RepoRefresh",  // Try with capital S
                "https://www.safarinow.com/admin/RepoRefresh",  // Alternative path
        };

        for (int attempt = 0; attempt < urlsToTry.length; attempt++) {
            String url = urlsToTry[attempt];
            System.out.println("Navigation attempt " + (attempt + 1) + ": " + url);

            try {
                driver.get(url);
                Thread.sleep(2000); // Wait for page to load

                String currentUrl = driver.getCurrentUrl();
                System.out.println("Current URL after navigation: " + currentUrl);
                System.out.println("Page title: " + driver.getTitle());

                // Check if we hit a server error
                if (currentUrl.contains("ServerError") || currentUrl.contains("Error")) {
                    System.err.println("✗ Server error encountered for URL: " + url);
                    System.err.println("Error URL: " + currentUrl);
                    continue; // Try next URL
                }

                // Check if we were redirected to login (session expired)
                if (currentUrl.toLowerCase().contains("login") || currentUrl.toLowerCase().contains("logon")) {
                    System.err.println("✗ Redirected to login page - session may have expired");
                    continue; // Try next URL
                }

                // Try to verify the page loaded correctly
                if (verifyRepoPageLoaded(repoPage)) {
                    System.out.println("✓ Successfully navigated to RepoRefresh page using: " + url);
                    return true;
                }

            } catch (Exception e) {
                System.err.println("✗ Exception during navigation attempt " + (attempt + 1) + ": " + e.getMessage());
            }
        }

        // If all direct URLs failed, try navigating through the UI
        System.out.println("Direct URL navigation failed, attempting UI navigation...");
        return attemptUINavigation(repoPage);
    }

    /**
     * Try to navigate to the RepoRefresh page through the UI instead of direct URL.
     */
    private boolean attemptUINavigation(RepoPage repoPage) {
        try {
            System.out.println("Attempting to find RepoRefresh through site navigation...");

            // Go to a known working staff page first
            driver.get("https://www.safarinow.com/staff/");
            Thread.sleep(2000);

            String currentUrl = driver.getCurrentUrl();
            System.out.println("Staff area URL: " + currentUrl);

            if (currentUrl.contains("ServerError") || currentUrl.contains("login")) {
                System.err.println("✗ Cannot access staff area - permission issue");
                return false;
            }

            // Look for links to RepoRefresh or similar functionality
            try {
                WebElement repoLink = driver.findElement(By.partialLinkText("Repo"));
                repoLink.click();
                Thread.sleep(2000);

                return verifyRepoPageLoaded(repoPage);

            } catch (NoSuchElementException e) {
                System.err.println("✗ Could not find Repo link in staff area");
                return false;
            }

        } catch (Exception e) {
            System.err.println("✗ UI navigation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify that the RepoRefresh page loaded correctly.
     */
    private boolean verifyRepoPageLoaded(RepoPage repoPage) {
        try {
            // First check if the main form exists
            if (!repoPage.waitForPageLoad()) {
                System.err.println("✗ frmMain element not found");
                return false;
            }

            // Then check for the specific button
            boolean buttonFound = repoPage.isRefreshAvailabilityBtnDisplayed();
            if (buttonFound) {
                System.out.println("✓ Refresh Availability button found - page loaded successfully");
                return true;
            } else {
                System.err.println("✗ Refresh Availability button not found");
                return false;
            }

        } catch (Exception e) {
            System.err.println("✗ Error verifying page load: " + e.getMessage());
            return false;
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

    private void waitForLoginCompletion() {
        try {
            System.out.println("Pausing for 3 seconds to allow server-side session to establish...");
            Thread.sleep(3000); // Increased from 1 to 3 seconds

            String currentUrl = driver.getCurrentUrl();
            System.out.println("URL after login pause: " + currentUrl);

            if (currentUrl.toLowerCase().contains("login")) {
                System.out.println("Still on login page, waiting additional time...");
                Thread.sleep(2000);
                currentUrl = driver.getCurrentUrl();
                System.out.println("URL after additional wait: " + currentUrl);
            }

            String pageTitle = driver.getTitle().toLowerCase();
            if (pageTitle.contains("error") || pageTitle.contains("invalid")) {
                throw new IllegalStateException("Login appears to have failed - error page detected");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Login wait interrupted", e);
        }
    }

    private void captureDebugInfo() {
        try {
            System.out.println("=== DEBUG INFO ON FAILURE ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Page source length: " + driver.getPageSource().length());

            String pageSource = driver.getPageSource().toLowerCase();
            if (pageSource.contains("error")) {
                System.out.println("⚠ Page source contains 'error'");
            }
            if (pageSource.contains("access denied")) {
                System.out.println("⚠ Page source contains 'access denied'");
            }
            if (pageSource.contains("unauthorized")) {
                System.out.println("⚠ Page source contains 'unauthorized'");
            }

            System.out.println("=== END DEBUG INFO ===");
        } catch (Exception e) {
            System.err.println("Error capturing debug info: " + e.getMessage());
        }
    }

    private ChromeOptions configureChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadlessEnvironment()) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
        }
        options.addArguments("--disable-web-security", "--disable-features=VizDisplayCompositor", "--remote-debugging-port=9222", "--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-extensions", "--disable-plugins");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return options;
    }

    private void setupWindowSize() {
        try {
            if (!isHeadlessEnvironment()) {
                driver.manage().window().maximize();
                System.out.println("✓ Window maximized");
            } else {
                System.out.println("✓ Running in headless mode with 1920x1080 resolution");
            }
        } catch (WebDriverException e) {
            System.out.println(STR."Window maximize failed, using manual sizing: \{e.getMessage()}");
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    private void verifyRepoPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Verify by checking the page title for "Repo Refresh"
            wait.until(ExpectedConditions.titleContains("Repo Refresh"));
            System.out.println("✓ Page title 'Repo Refresh' confirmed.");

            // Verify by checking for the presence of the main button
            RepoPage repoPage = new RepoPage(driver);
            Assert.assertTrue(repoPage.isRefreshAvailabilityBtnDisplayed(), "RepoRefresh page loaded, but 'Refresh Availability' button was not found.");
            System.out.println("✓ 'Refresh Availability' button found.");

        } catch (Exception e) {
            captureDebugInfo(); // Capture debug info on failure
            throw new IllegalStateException("Failed to verify that the RepoRefresh page loaded correctly.", e);
        }
    }

    private boolean isHeadlessEnvironment() {
        return System.getenv("CI") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                Boolean.parseBoolean(System.getProperty("java.awt.headless", "false"));
    }
}
