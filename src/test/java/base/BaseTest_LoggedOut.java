// In: base/BaseTest_LoggedOut.java
package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

/**
 * A base class for tests that require a clean, LOGGED-OUT browser session.
 * It now uses the WebDriverLifecycleManager for robust cleanup.
 */
public abstract class BaseTest_LoggedOut {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(configureChromeOptions());
        // Register the driver with the lifecycle manager
        WebDriverLifecycleManager.register(driver);

        setupWindowSize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass(alwaysRun = true)
    public void teardown() {
        // Delegate cleanup to the lifecycle manager
        WebDriverLifecycleManager.quitDriver(driver);
    }

    // ... all other private methods remain the same ...
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