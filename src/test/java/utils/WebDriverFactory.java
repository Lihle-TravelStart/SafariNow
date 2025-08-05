package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {
    
    public static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Configure for different environments
        if (isCI()) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Chrome 137 compatibility options
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--remote-debugging-port=9222");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        WebDriver driver = new ChromeDriver(options);
        
        // Safe window setup
        setupWindow(driver);
        
        return driver;
    }
    
    private static void setupWindow(WebDriver driver) {
        try {
            if (!isCI()) {
                driver.manage().window().maximize();
            }
        } catch (WebDriverException e) {
            System.out.println("Window maximize failed, using fallback: " + e.getMessage());
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }
    
    private static boolean isCI() {
        return System.getenv("CI") != null || 
               System.getenv("GITHUB_ACTIONS") != null;
    }
}
