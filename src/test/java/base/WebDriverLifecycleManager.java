package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * A singleton manager for the WebDriver lifecycle, ensuring only one
 * instance is created per test suite run.
 */
public class WebDriverLifecycleManager {

    private static WebDriver driverInstance;

    // Private constructor to prevent direct instantiation.
    private WebDriverLifecycleManager() {}

    /**
     * Initializes the singleton WebDriver instance. Throws an error if called more than once.
     */
    public static synchronized void initDriver() {
        if (driverInstance != null) {
            System.err.println("WARN: initDriver called but a driver instance already exists.");
            return;
        }
        System.out.println("=== Initializing WebDriver for the test suite. ===");
        WebDriverManager.chromedriver().setup();
        driverInstance = new ChromeDriver(configureChromeOptions());
        setupWindowSize();
        System.out.println("✓ New WebDriver instance created and configured.");
    }

    /**
     * Retrieves the singleton WebDriver instance.
     * @return The shared WebDriver instance.
     * @throws IllegalStateException if the driver has not been initialized by the SuiteListener.
     */
    public static synchronized WebDriver getDriver() {
        if (driverInstance == null) {
            throw new IllegalStateException("WebDriver has not been initialized. Please ensure the SuiteListener is configured in your testng.xml.");
        }
        return driverInstance;
    }

    /**
     * Quits the singleton WebDriver instance if it exists.
     */
    public static synchronized void quitDriver() {
        if (driverInstance != null) {
            try {
                System.out.println(STR."LifecycleManager: Cleaning up WebDriver instance \{driverInstance.hashCode()}");
                driverInstance.quit();
            } catch (Exception e) {
                System.err.println(STR."Error while quitting driver: \{e.getMessage()}");
            } finally {
                driverInstance = null;
            }
        }
    }

    // --- Private Helper Methods ---

    private static ChromeOptions configureChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadlessEnvironment()) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
        }
        options.addArguments("--disable-web-security", "--disable-features=VizDisplayCompositor", "--remote-debugging-port=9222", "--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return options;
    }

    private static void setupWindowSize() {
        try {
            if (!isHeadlessEnvironment()) {
                driverInstance.manage().window().maximize();
                System.out.println("✓ Window maximized");
            }
        } catch (WebDriverException e) {
            System.out.println(STR."Window maximize failed, using manual sizing: \{e.getMessage()}");
            driverInstance.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    private static boolean isHeadlessEnvironment() {
        return System.getenv("CI") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                Boolean.parseBoolean(System.getProperty("java.awt.headless", "false"));
    }
}