package data;

/**
 * Data model for test destinations
 */
public class TestDestination {
    private final String name;
    private final String type;
    private final int expectedMinProperties;
    private final String priority;
    
    public TestDestination(String name, String type, int expectedMinProperties, String priority) {
        this.name = name;
        this.type = type;
        this.expectedMinProperties = expectedMinProperties;
        this.priority = priority;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public int getExpectedMinProperties() { return expectedMinProperties; }
    public String getPriority() { return priority; }
    
    @Override
    public String toString() {
        return String.format("TestDestination{name='%s', type='%s', priority='%s'}", name, type, priority);
    }
}
