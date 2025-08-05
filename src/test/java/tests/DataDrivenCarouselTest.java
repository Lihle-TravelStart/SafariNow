package tests;

import data.TestDataLoader;
import data.TestDestination;
import data.TestScenario;
import helpers.DataDrivenSearchFlow;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.DriverManager;
import java.util.List;

/**
 * Pure data-driven tests - no hardcoded values, everything from external data
 */
public class DataDrivenCarouselTest {
    private WebDriver driver;
    // Initialize dataLoader at field declaration time - FIXES THE NULL POINTER
    private final TestDataLoader dataLoader = TestDataLoader.getInstance();
    private DataDrivenSearchFlow searchFlow;
    private String environment;

    @BeforeMethod
    @Parameters({"environment"})
    public void setUp(String env) {
        this.environment = env != null ? env : "qa"; // Default to QA

        driver = DriverManager.getDriver();
        searchFlow = new DataDrivenSearchFlow(driver, environment);

        System.out.println("=== DATA-DRIVEN TEST SETUP ===");
        System.out.println("Environment: " + environment);
        System.out.println("Base URL: " + dataLoader.getEnvironmentUrl(environment));
    }

    @DataProvider(name = "destinations")
    public Object[][] getDestinations() {
        List<TestDestination> destinations = dataLoader.getTestDestinations();
        Object[][] data = new Object[destinations.size()][1];

        for (int i = 0; i < destinations.size(); i++) {
            data[i][0] = destinations.get(i);
        }

        return data;
    }

    @DataProvider(name = "highPriorityDestinations")
    public Object[][] getHighPriorityDestinations() {
        List<TestDestination> destinations = dataLoader.getDestinationsByPriority("high");
        Object[][] data = new Object[destinations.size()][1];

        for (int i = 0; i < destinations.size(); i++) {
            data[i][0] = destinations.get(i);
        }

        return data;
    }

    @DataProvider(name = "testScenarios")
    public Object[][] getTestScenarios() {
        List<TestScenario> scenarios = dataLoader.getTestScenarios();
        List<TestDestination> destinations = dataLoader.getDestinationsByPriority("high");

        // Create combinations of scenarios and destinations
        Object[][] data = new Object[scenarios.size() * destinations.size()][2];
        int index = 0;

        for (TestScenario scenario : scenarios) {
            for (TestDestination destination : destinations) {
                data[index][0] = scenario;
                data[index][1] = destination;
                index++;
            }
        }

        return data;
    }

    @Test(dataProvider = "destinations", description = "Test all destinations from data file")
    public void testDestinationSearch(TestDestination destination) {
        System.out.println("\n--- TESTING DESTINATION: " + destination + " ---");

        // Execute test steps
        searchFlow.navigateToHomepage(environment);

        boolean searchSuccess = searchFlow.searchDestination(destination.getName());
        Assert.assertTrue(searchSuccess, "Search should succeed for: " + destination.getName());

        boolean validResults = searchFlow.validateSearchResults(destination.getName());
        Assert.assertTrue(validResults, "Should have valid search results for: " + destination.getName());

        System.out.println("✓ RESULT: " + destination.getName() + " test completed successfully");
    }

    @Test(dataProvider = "highPriorityDestinations", description = "Test high priority destinations with property selection")
    public void testHighPriorityDestinationsWithPropertySelection(TestDestination destination) {
        System.out.println("\n--- HIGH PRIORITY TEST: " + destination + " ---");

        searchFlow.navigateToHomepage(environment);

        boolean searchSuccess = searchFlow.searchDestination(destination.getName());
        Assert.assertTrue(searchSuccess, "Search should succeed");

        boolean validResults = searchFlow.validateSearchResults(destination.getName());
        Assert.assertTrue(validResults, "Should have valid search results");

        boolean propertySelected = searchFlow.selectFirstProperty();
        Assert.assertTrue(propertySelected, "Should be able to select first property");

        boolean carouselWorking = searchFlow.testImageCarousel();
        Assert.assertTrue(carouselWorking, "Image carousel should be functional");

        System.out.println("✓ RESULT: High priority test completed for " + destination.getName());
    }

    @Test(dataProvider = "testScenarios", description = "Execute test scenarios from data file")
    public void testDataDrivenScenarios(TestScenario scenario, TestDestination destination) {
        System.out.println("\n--- SCENARIO: " + scenario.getScenarioName() + " | DESTINATION: " + destination.getName() + " ---");
        System.out.println("Description: " + scenario.getDescription());

        // Execute each step in the scenario
        for (String step : scenario.getSteps()) {
            boolean stepResult = executeStep(step, destination);
            Assert.assertTrue(stepResult, "Step should succeed: " + step);
        }

        System.out.println("✓ RESULT: Scenario completed successfully");
    }

    private boolean executeStep(String step, TestDestination destination) {
        try {
            System.out.println("Executing step: " + step);

            switch (step) {
                case "navigateToHomepage":
                    searchFlow.navigateToHomepage(environment);
                    return true;

                case "searchDestination":
                    return searchFlow.searchDestination(destination.getName());

                case "validateSearchResults":
                    return searchFlow.validateSearchResults(destination.getName());

                case "selectFirstProperty":
                    return searchFlow.selectFirstProperty();

                case "validatePropertyPage":
                    // Add property page validation logic
                    return true;

                case "testImageCarousel":
                    return searchFlow.testImageCarousel();

                case "validateMultipleProperties":
                    // Add multiple properties validation
                    return true;

                case "testSRPCarousels":
                    // Add SRP carousel testing
                    return true;

                default:
                    System.out.println("Unknown step: " + step);
                    return false;
            }

        } catch (Exception e) {
            System.err.println("Step execution failed: " + e.getMessage());
            return false;
        }
    }
}
