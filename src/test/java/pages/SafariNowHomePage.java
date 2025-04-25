package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SafariNowHomePage {
    private WebDriver driver;
    // Locators
    private final By loginButton = By.xpath("//*[@id='MenuItems']/ul/li[3]/a");
    private final By menuButton = By.xpath("//*[@id='MenuAccount']/a/span");
    private final By searchButton = By.id("btnSearch");
    private final By popularAccommodationLocator = By.cssSelector(".popular-accommodations .accommodation-item");
    private final By bookOrEnquireButtonLocator = By.xpath(".//button[contains(text(), 'Book Now') or contains(text(), 'Enquire')]");
    private final By suggestResults = By.xpath("//div[normalize-space()='" + "Cape Town" + "']");
    public By destinationInput = By.id("SearchFilterFilterSearchTerm");
    // New Locators for the Deals and Offers Carousel (Swiper)
    public By dealsOffersCarouselContainer = By.cssSelector("[id='0-swiperContainer']");
    public By dealsOffersNextButton = By.cssSelector(".swiper-button-next.swiper-next-0");
    public By dealsOffersPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-0");
    public By dealsOffersCarouselItems = By.cssSelector("[id='0-swiperContainer'].swiper-slide img");
    public String dealsOffersActiveImageClass = "swiper-slide-active";
    //New locators
    public By popularDestinationsCarouselContainer = By.cssSelector("#\\31-swiperContainer");
    public By popularDestinationsNextButton = By.cssSelector(".swiper-button-next.swiper-next-1");
    public By popularDestinationsPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-1");
    public By popularDestinationsCarouselItems = By.cssSelector("[id='31-swiperContainer'] .swiper-slide img");
    public By weekendGetawaysCarouselContainer = By.cssSelector("[id='33-swiperContainer']");
    public By weekendGetawaysNextButton = By.cssSelector(".swiper-button-next.swiper-next-2");
    public By weekendGetawaysPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-2");
    public By weekendGetawaysCarouselItems = By.cssSelector("[id='33-swiperContainer'] .swiper-slide img");
    public By cityTripsCarouselContainer = By.cssSelector("[id='35-swiperContainer']");
    public By cityTripsNextButton = By.cssSelector(".swiper-button-next.swiper-next-3");
    public By cityTripsPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-3");
    public By cityTripsCarouselItems = By.cssSelector("[id='35-swiperContainer'] .swiper-slide img");
    public By kwazuluNatalBeachEscapesCarouselContainer = By.cssSelector("[id='37-swiperContainer']");
    public By kwazuluNatalBeachEscapesNextButton = By.cssSelector(".swiper-button-next.swiper-next-4");
    public By kwazuluNatalBeachEscapesPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-4");
    public By kwazuluNatalBeachEscapesCarouselItems = By.cssSelector("[id='37-swiperContainer'] .swiper-slide img");
    public By wildlifeAdventuresCarouselContainer = By.cssSelector("[id='39-swiperContainer']");
    public By wildlifeAdventuresNextButton = By.cssSelector(".swiper-button-next.swiper-next-5");
    public By wildlifeAdventuresPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-5");
    public By wildlifeAdventuresCarouselItems = By.cssSelector("[id='39-swiperContainer'] .swiper-slide img");
    public By exploreTheGardenRouteCarouselContainer = By.cssSelector("[id='41-swiperContainer']");
    public By exploreTheGardenRouteNextButton = By.cssSelector(".swiper-button-next.swiper-next-6");
    public By exploreTheGardenRoutePrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-6");
    public By exploreTheGardenRouteCarouselItems = By.cssSelector("[id='41-swiperContainer'] .swiper-slide img");
    public By amazingCountryEscapesCarouselContainer = By.cssSelector("[id='43-swiperContainer']");
    public By amazingCountryEscapesNextButton = By.cssSelector(".swiper-button-next.swiper-next-7");
    public By amazingCountryEscapesPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-7");
    public By amazingCountryEscapesCarouselItems = By.cssSelector("[id='43-swiperContainer'] .swiper-slide img");
    public By westCoastGetawaysCarouselContainer = By.cssSelector("[id='45-swiperContainer']");
    public By westCoastGetawaysNextButton = By.cssSelector(".swiper-button-next.swiper-next-8");
    public By westCoastGetawaysPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-8");
    public By westCoastGetawaysCarouselItems = By.cssSelector("[id='45-swiperContainer'] .swiper-slide img");
    public By discoverTheWinelandsCarouselContainer = By.cssSelector("[id='47-swiperContainer']");
    public By discoverTheWinelandsNextButton = By.cssSelector(".swiper-button-next.swiper-next-9");
    public By discoverTheWinelandsPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-9");
    public By discoverTheWinelandsCarouselItems = By.cssSelector("[id='47-swiperContainer'] .swiper-slide img");
    public By incredibleRoadTripsCarouselContainer = By.cssSelector("[id='49-swiperContainer']");
    public By incredibleRoadTripsNextButton = By.cssSelector(".swiper-button-next.swiper-next-10");
    public By incredibleRoadTripsPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-10");
    public By incredibleRoadTripsCarouselItems = By.cssSelector("[id='49-swiperContainer'] .swiper-slide img");
    public By stopOversCarouselContainer = By.cssSelector("[id='51-swiperContainer']");
    public By stopOversNextButton = By.cssSelector(".swiper-button-next.swiper-next-11");
    public By stopOversPrevButton = By.cssSelector(".swiper-button-prev.swiper-prev-11");
    public By stopOversCarouselItems = By.cssSelector("[id='51-swiperContainer'] .swiper-slide img");
    public By accomodationButton = By.xpath("//a[@href='/accommodation-in-south-africa.aspx']");
    private WebDriverWait wait;

    public void enterDestination(String destination) {
        WebElement destinationField = driver.findElement(destinationInput);
        destinationField.clear();
        destinationField.sendKeys(destination);
    }

    public void clickSearchButton() {
        driver.findElement(searchButton).click();
    }

    public void selectPopularAccommodation(int propertyIndex) {
        // Wait until the popular accommodations section is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(popularAccommodationLocator));

        // Get the list of popular accommodations
        List<WebElement> popularAccommodations = driver.findElements(popularAccommodationLocator);

        // Check if the index is within bounds
        if (propertyIndex < 0 || propertyIndex >= popularAccommodations.size()) {
            throw new IllegalArgumentException("Invalid property index");
        }

        // Select the property by clicking its "Book Now" or "Enquire" button
        WebElement selectedProperty = popularAccommodations.get(propertyIndex);
        WebElement bookOrEnquireButton = selectedProperty.findElement(bookOrEnquireButtonLocator);
        bookOrEnquireButton.click();
    }
    public WebElement selectDropdownOption(String optionText) {
        String xpath = "//*[@id=\"scrollable-dropdown-menu\"]/span/div/div/div[2]/div/div[1]";
        return driver.findElement(By.xpath(xpath));
    }
    //div[@class='suggestion u-cf Typeahead-suggestion Typeahead-selectable tt-suggestion tt-selectable tt-cursor']//div[@class='suggestion-location'][normalize-space()='Western Cape']
    //div[@class='suggestion u-cf Typeahead-suggestion Typeahead-selectable tt-suggestion tt-selectable tt-cursor']//div[@class='suggestion-name'][normalize-space()='Cape Town']
    //div[normalize-space()='Cape Town 4U']

    public void selectAutoSuggestion(By inputLocator,String inputText) {
        WebElement inputField = driver.findElement(inputLocator);
        inputField.clear();
        inputField.sendKeys(inputText);

        // Wait for the auto-suggestions to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestResults));

        // Click on the first suggestion (or you can add logic to select a specific one)
        List<WebElement> suggestions = driver.findElements(suggestResults);

        if (!suggestions.isEmpty()) {
            suggestions.get(0).click();  // Select the first suggestion
        }
    }
    public void clickLoginButton() {
        WebElement loginBtn = driver.findElement(loginButton);
        loginBtn.click();
    }
    public void clickmenuButton() {
        WebElement menuBtn = driver.findElement(menuButton);
        menuBtn.click();
    }
    public void navigateToOtherURL(String url) {
        driver.navigate().to(url);
    }

    public SafariNowHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateHome() {
        driver.get("https://www.safarinow.com/");
    }

    public void clickOnAccomodation() {
        WebElement accomodationBtn = driver.findElement(accomodationButton);
        accomodationBtn.click();
    }
}