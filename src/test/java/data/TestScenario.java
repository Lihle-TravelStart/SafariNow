package data;

import java.util.List;

/**
 * Data model for test scenarios
 */
public class TestScenario {
    private final String scenarioName;
    private final String description;
    private final List<String> steps;
    
    public TestScenario(String scenarioName, String description, List<String> steps) {
        this.scenarioName = scenarioName;
        this.description = description;
        this.steps = steps;
    }
    
    public String getScenarioName() { return scenarioName; }
    public String getDescription() { return description; }
    public List<String> getSteps() { return steps; }
    
    @Override
    public String toString() {
        return String.format("TestScenario{name='%s', steps=%d}", scenarioName, steps.size());
    }
}
