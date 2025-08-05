// In: base/WebDriverLifecycleManager.java
package base;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A centralized manager for the lifecycle of all WebDriver instances.
 * It tracks all active drivers and ensures they are all terminated
 * via a JVM shutdown hook, preventing orphaned browser windows.
 */
public final class WebDriverLifecycleManager {

    // A thread-safe list to hold all active WebDriver instances.
    private static final List<WebDriver> activeDrivers = Collections.synchronizedList(new ArrayList<>());

    // A single, universal shutdown hook.
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(WebDriverLifecycleManager::quitAllDrivers));
    }

    // Private constructor to prevent instantiation.
    private WebDriverLifecycleManager() {}

    /**
     * Registers a new WebDriver instance to be tracked.
     * This should be called immediately after a driver is created.
     *
     * @param driver The WebDriver instance to track.
     */
    public static void register(WebDriver driver) {
        if (driver != null) {
            activeDrivers.add(driver);
        }
    }

    /**
     * Quits a specific WebDriver instance and removes it from tracking.
     * This is the standard method for clean teardown.
     *
     * @param driver The WebDriver instance to quit.
     */
    public static void quitDriver(WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            System.out.println(STR."LifecycleManager: Cleaning up driver \{driver.hashCode()}");
            driver.quit();
        } catch (Exception e) {
            System.err.println(STR."LifecycleManager: Error during WebDriver quit: \{e.getMessage()}");
        } finally {
            activeDrivers.remove(driver);
        }
    }

    /**
     * The failsafe method called by the shutdown hook.
     * It iterates through any remaining drivers that were not cleanly
     * closed and forcefully terminates them.
     */
    private static void quitAllDrivers() {
        if (!activeDrivers.isEmpty()) {
            System.out.println(STR."Shutdown Hook: Forcing cleanup of \{activeDrivers.size()} remaining WebDriver instance(s)...");
            // Create a copy to avoid ConcurrentModificationException while iterating
            List<WebDriver> driversToQuit = new ArrayList<>(activeDrivers);
            for (WebDriver driver : driversToQuit) {
                quitDriver(driver);
            }
            System.out.println("Shutdown Hook: All remaining drivers terminated.");
        }
    }
}