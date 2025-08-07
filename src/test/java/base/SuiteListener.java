package base;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * This listener manages the global setup and teardown of the WebDriver instance
 * for the entire test suite, ensuring one browser per run.
 */
public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("=== TEST SUITE STARTING - INITIALIZING WEBDRIVER ===");
        WebDriverLifecycleManager.initDriver();
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("=== TEST SUITE FINISHED - QUITTING WEBDRIVER ===");
        WebDriverLifecycleManager.quitDriver();
    }
}