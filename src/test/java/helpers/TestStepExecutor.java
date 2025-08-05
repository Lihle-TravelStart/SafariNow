package helpers;

import data.TestDestination;
import helpers.DataDrivenSearchFlow;

/**
 * Handles execution of individual test steps
 */
public class TestStepExecutor {
    private final DataDrivenSearchFlow searchFlow;
    private final String environment;
    
    public TestStepExecutor(DataDrivenSearchFlow searchFlow, String environment) {
        this.searchFlow = searchFlow;
        this.environment = environment;
    }
    
    /**
     * Execute a single test step
     */
    public boolean executeStep(String step, TestDestination destination) {
        try {
            System.out.println(STR."Executing step: \{step}");
            
            return switch (step) {
                case "navigateToHomepage" -> executeNavigateToHomepage();
                case "searchDestination" -> executeSearchDestination(destination);
                case "validateSearchResults" -> executeValidateSearchResults(destination);
                case "selectFirstProperty" -> executeSelectFirstProperty();
                case "validatePropertyPage" -> executeValidatePropertyPage();
                case "testImageCarousel" -> executeTestImageCarousel();
                case "validateMultipleProperties" -> executeValidateMultipleProperties();
                case "testSRPCarousels" -> executeTestSRPCarousels();
                default -> {
                    System.out.println(STR."Unknown step: \{step}");
                    yield false;
                }
            };
            
        } catch (Exception e) {
            System.err.println(STR."Step execution failed: \{e.getMessage()}");
            return false;
        }
    }
    
    // Individual step implementations
    private boolean executeNavigateToHomepage() {
        searchFlow.navigateToHomepage(environment);
        return true;
    }
    
    private boolean executeSearchDestination(TestDestination destination) {
        return searchFlow.searchDestination(destination.getName());
    }
    
    private boolean executeValidateSearchResults(TestDestination destination) {
        return searchFlow.validateSearchResults(destination.getName());
    }
    
    private boolean executeSelectFirstProperty() {
        return searchFlow.selectFirstProperty();
    }
    
    private boolean executeValidatePropertyPage() {
        // TODO: Implement property page validation logic
        System.out.println("Property page validation - placeholder implementation");
        return true;
    }
    
    private boolean executeTestImageCarousel() {
        return searchFlow.testImageCarousel();
    }
    
    private boolean executeValidateMultipleProperties() {
        // TODO: Implement multiple properties validation
        System.out.println("Multiple properties validation - placeholder implementation");
        return true;
    }
    
    private boolean executeTestSRPCarousels() {
        // TODO: Implement SRP carousel testing
        System.out.println("SRP carousel testing - placeholder implementation");
        return true;
    }
}
