package data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * REFACTORED: Loads test data from an external JSON file using a robust, lazy-loading singleton pattern.
 * The data is only loaded from the file on the first request, preventing crashes if the file is not needed.
 */
public class TestDataLoader {
    private JsonNode testData; // Now an instance variable
    private static TestDataLoader instance;

    // Private constructor to enforce the singleton pattern
    private TestDataLoader() {}

    /**
     * Ensures that the test data is loaded from the JSON file.
     * This method is called before any data access to implement lazy loading.
     * It only loads the data once.
     */
    private void ensureTestDataIsLoaded() {
        // Only load if it hasn't been loaded yet
        if (this.testData != null) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = TestDataLoader.class.getClassLoader().getResourceAsStream("test-data.json");

            if (inputStream == null) {
                // This exception will now be thrown only when a test actually needs the data.
                throw new RuntimeException("test-data.json file not found in classpath. Please ensure it exists in src/test/resources.");
            }

            this.testData = mapper.readTree(inputStream);
            System.out.println("✓ Test data loaded successfully on first use.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the singleton instance of the TestDataLoader.
     * This is thread-safe.
     * @return The single instance of the TestDataLoader.
     */
    public static synchronized TestDataLoader getInstance() {
        if (instance == null) {
            instance = new TestDataLoader();
        }
        return instance;
    }

    /**
     * Get environment configuration
     */
    public String getEnvironmentUrl(String environment) {
        ensureTestDataIsLoaded();
        return testData.path("environments").path(environment).path("baseUrl").asText();
    }

    /**
     * Get all test destinations
     */
    public List<TestDestination> getTestDestinations() {
        ensureTestDataIsLoaded();
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
        // This method reuses getTestDestinations, which already ensures data is loaded.
        return getTestDestinations().stream()
                .filter(dest -> dest.getPriority().equals(priority))
                .toList();
    }

    /**
     * Get test scenarios
     */
    public List<TestScenario> getTestScenarios() {
        ensureTestDataIsLoaded();
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
        ensureTestDataIsLoaded();
        return testData.path("selectors").path(category).path(selectorName).asText();
    }

    /**
     * Get configuration value
     */
    public int getConfigInt(String category, String configName) {
        ensureTestDataIsLoaded();
        return testData.path("configuration").path(category).path(configName).asInt();
    }

    public double getConfigDouble(String category, String configName) {
        ensureTestDataIsLoaded();
        return testData.path("configuration").path(category).path(configName).asDouble();
    }

    public String getConfigString(String category, String configName) {
        ensureTestDataIsLoaded();
        return testData.path("configuration").path(category).path(configName).asText();
    }
}