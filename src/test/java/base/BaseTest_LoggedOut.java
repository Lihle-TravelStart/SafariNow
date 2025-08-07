package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;

/**
 * A base class for tests that require a clean, LOGGED-OUT browser session.
 * It now correctly gets the shared WebDriver instance from the central manager.
 */
public abstract class BaseTest_LoggedOut {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        // Get the singleton driver instance from the manager.
        // The driver is created by the SuiteListener before this class's tests run.
        this.driver = WebDriverLifecycleManager.getDriver();
    }
}