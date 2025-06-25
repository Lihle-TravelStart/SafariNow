package base;

import io.github.bonigarcia.webdrivermanager.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Add Chrome options for better compatibility with Chrome 137
        if (isHeadlessEnvironment()) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--remote-debugging-port=9222");
        
        // Disable CDP-related features that cause issues with Chrome 137
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        
        // Safe window management - handles Chrome 137 CDP issues
        setupWindowSize();
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private void setupWindowSize() {
        try {
            if (!isHeadlessEnvironment()) {
                driver.manage().window().maximize();
            }
        } catch (WebDriverException e) {
            // If maximize fails due to CDP issues, set window size manually
            System.out.println("Window maximize failed, using manual window sizing: " + e.getMessage());
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    private boolean isHeadlessEnvironment() {
        // Check if running in CI/CD environment
        return System.getenv("CI") != null || 
               System.getenv("GITHUB_ACTIONS") != null ||
               System.getProperty("java.awt.headless", "false").equals("true");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

