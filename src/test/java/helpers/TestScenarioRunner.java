package helpers;

import data.TestDestination;
import data.TestScenario;
import helpers.DataDrivenSearchFlow;

/**
 * Orchestrates execution of complete test scenarios
 */
public class TestScenarioRunner {
    private final TestStepExecutor stepExecutor;
    
    public TestScenarioRunner(DataDrivenSearchFlow searchFlow, String environment) {
        this.stepExecutor = new TestStepExecutor(searchFlow, environment);
    }
    
    /**
     * Execute a complete test scenario
     */
    public boolean executeScenario(TestScenario scenario, TestDestination destination) {
        System.out.println("\n--- SCENARIO: " + scenario.getScenarioName() + " | DESTINATION: " + destination.getName() + " ---");
        System.out.println("Description: " + scenario.getDescription());
        
        // Execute each step in the scenario
        for (String step : scenario.getSteps()) {
            boolean stepResult = stepExecutor.executeStep(step, destination);
            if (!stepResult) {
                System.err.println("❌ Step failed: " + step);
                return false;
            }
            System.out.println("✓ Step completed: " + step);
        }
        
        System.out.println("✓ RESULT: Scenario completed successfully");
        return true;
    }
    
    /**
     * Execute a simple destination search test
     */
    public boolean executeDestinationSearch(TestDestination destination) {
        System.out.println("\n--- TESTING DESTINATION: " + destination + " ---");
        
        boolean success = true;
        success &= stepExecutor.executeStep("navigateToHomepage", destination);
        success &= stepExecutor.executeStep("searchDestination", destination);
        success &= stepExecutor.executeStep("validateSearchResults", destination);
        
        if (success) {
            System.out.println("✓ RESULT: " + destination.getName() + " test completed successfully");
        } else {
            System.out.println("❌ RESULT: " + destination.getName() + " test failed");
        }
        
        return success;
    }
    
    /**
     * Execute a high priority destination test with property selection
     */
    public boolean executeHighPriorityDestinationTest(TestDestination destination) {
        System.out.println("\n--- HIGH PRIORITY TEST: " + destination + " ---");
        
        boolean success = true;
        success &= stepExecutor.executeStep("navigateToHomepage", destination);
        success &= stepExecutor.executeStep("searchDestination", destination);
        success &= stepExecutor.executeStep("validateSearchResults", destination);
        success &= stepExecutor.executeStep("selectFirstProperty", destination);
        success &= stepExecutor.executeStep("testImageCarousel", destination);
        
        if (success) {
            System.out.println("✓ RESULT: High priority test completed for " + destination.getName());
        } else {
            System.out.println("❌ RESULT: High priority test failed for " + destination.getName());
        }
        
        return success;
    }
}
