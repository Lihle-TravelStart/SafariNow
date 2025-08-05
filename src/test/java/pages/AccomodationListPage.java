package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates all elements and actions for the accommodation search results list page.
 * This version uses precise locators and a robust, defensive waiting strategy for stability.
 */
public class AccomodationListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(tagName = "h1")
    private WebElement pageHeader;

    // --- Constructor ---

    public AccomodationListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // --- Page Actions & Verifications ---

    public String getPageHeaderText() {
        return wait.until(ExpectedConditions.visibilityOf(pageHeader)).getText();
    }

    public List<WebElement> getPropertyCards() {
        By propertyCardLocator = By.xpath("//div[contains(@class, 'content-accommodation')]/..");
        System.out.println("Waiting for property cards to be present on the page using the correct locator...");
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(propertyCardLocator));
        if (!cards.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOf(cards.get(0)));
        }
        System.out.println(STR."✓ Found \{cards.size()} property cards on the page.");
        return cards;
    }

    public void selectFirstProperty() {
        System.out.println("Waiting for property cards to become available to select...");
        List<WebElement> propertyCards = getPropertyCards();

        if (!propertyCards.isEmpty()) {
            System.out.println("Selecting the first property from the results list.");
            propertyCards.get(0).click();
            System.out.println("✓ Clicked the first property in the list.");
        } else {
            throw new IllegalStateException("No properties found in the list to select.");
        }
    }

    public String getPropertyName(WebElement card) {
        return card.findElement(By.cssSelector("h3 > a.hub-title")).getText();
    }

    public List<WebElement> findPropertyImages(WebElement card) {
        return card.findElements(By.cssSelector("img[data-src]"));
    }

    public Optional<WebElement> findOriginalPrice(WebElement card) {
        return card.findElements(By.cssSelector(".price-struck, .linethrough")).stream().findFirst();
    }

    public Optional<WebElement> findDiscountedPrice(WebElement card) {
        return card.findElements(By.cssSelector(".price-no-special, .price-special")).stream().findFirst();
    }

    public Optional<WebElement> findDiscountPercentage(WebElement card) {
        return card.findElements(By.cssSelector(".starburst span, .special-tag")).stream().findFirst();
    }

    public int getTotalResultsCount() {
        By summaryLocator = By.cssSelector("div.l-fl.l-pad-r-m");
        WebElement summaryElement = wait.until(ExpectedConditions.visibilityOfElementLocated(summaryLocator));
        String summaryText = summaryElement.getText(); // e.g., "2400+ accommodation listings..."
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(summaryText);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalStateException("Could not parse total results count from text: " + summaryText);
    }

    /**
     * REFACTORED: Applies a filter by clicking the associated clickable element (link or checkbox).
     * This method uses a robust, combined XPath to handle multiple HTML structures.
     * @param filterLabel The visible text of the filter you want to apply (e.g., "Swimming Pool", "Special Deals Only").
     */
    public void applyFilterByLabel(String filterLabel) {
        // This new XPath looks for an <a> tag containing the text OR an <input> associated with a <label> containing the text.
        // This handles both link-based filters and checkbox-based filters.
        String universalFilterXPath = String.format(
                "//a[contains(., '%1$s') and contains(@class, 'lnk-filter')] | //input[@id = //label[contains(., '%1$s')]/@for]",
                filterLabel
        );
        By filterLocator = By.xpath(universalFilterXPath);

        System.out.println(STR."Attempting to apply filter: '\{filterLabel}' with new universal locator.");

        // First, wait for the element to be present in the DOM and scroll to it.
        WebElement filterElement = wait.until(ExpectedConditions.presenceOfElementLocated(filterLocator));
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", filterElement);
            Thread.sleep(250); // Small pause for scrolling to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Now, wait for it to be clickable and click it.
        wait.until(ExpectedConditions.elementToBeClickable(filterElement)).click();

        System.out.println(STR."✓ Clicked filter: '\{filterLabel}'");
    }
}