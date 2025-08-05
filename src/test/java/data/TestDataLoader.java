package data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads all test data from external JSON file
 */
public class TestDataLoader {
    private static JsonNode testData;
    private static TestDataLoader instance;
    
    static {
        loadTestData();
    }
    
    private static void loadTestData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = TestDataLoader.class.getClassLoader().getResourceAsStream("test-data.json");
            
            if (inputStream == null) {
                throw new RuntimeException("test-data.json file not found in classpath");
            }
            
            testData = mapper.readTree(inputStream);
            System.out.println("✓ Test data loaded successfully");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + e.getMessage(), e);
        }
    }
    
    public static TestDataLoader getInstance() {
        if (instance == null) {
            instance = new TestDataLoader();
        }
        return instance;
    }
    
    /**
     * Get environment configuration
     */
    public String getEnvironmentUrl(String environment) {
        return testData.path("environments").path(environment).path("baseUrl").asText();
    }
    
    /**
     * Get all test destinations
     */
    public List<TestDestination> getTestDestinations() {
        List<TestDestination> destinations = new ArrayList<>();
        JsonNode destinationsNode = testData.path("testDestinations");
        
        for (JsonNode destNode : destinationsNode) {
            TestDestination destination = new TestDestination(
                destNode.path("name").asText(),
                destNode.path("type").asText(),
                destNode.path("expectedMinProperties").asInt(),
                destNode.path("priority").asText()
            );
            destinations.add(destination);
        }
        
        return destinations;
    }
    
    /**
     * Get destinations by priority
     */
    public List<TestDestination> getDestinationsByPriority(String priority) {
        return getTestDestinations().stream()
            .filter(dest -> dest.getPriority().equals(priority))
            .toList();
    }
    
    /**
     * Get test scenarios
     */
    public List<TestScenario> getTestScenarios() {
        List<TestScenario> scenarios = new ArrayList<>();
        JsonNode scenariosNode = testData.path("testScenarios");
        
        for (JsonNode scenarioNode : scenariosNode) {
            List<String> steps = new ArrayList<>();
            for (JsonNode stepNode : scenarioNode.path("steps")) {
                steps.add(stepNode.asText());
            }
            
            TestScenario scenario = new TestScenario(
                scenarioNode.path("scenarioName").asText(),
                scenarioNode.path("description").asText(),
                steps
            );
            scenarios.add(scenario);
        }
        
        return scenarios;
    }
    
    /**
     * Get selector by category and name
     */
    public String getSelector(String category, String selectorName) {
        return testData.path("selectors").path(category).path(selectorName).asText();
    }
    
    /**
     * Get configuration value
     */
    public int getConfigInt(String category, String configName) {
        return testData.path("configuration").path(category).path(configName).asInt();
    }
    
    public double getConfigDouble(String category, String configName) {
        return testData.path("configuration").path(category).path(configName).asDouble();
    }
    
    public String getConfigString(String category, String configName) {
        return testData.path("configuration").path(category).path(configName).asText();
    }
}
