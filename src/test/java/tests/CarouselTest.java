package tests;

import helpers.CarouselHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.PropertyPage;
import pages.SafariNowHomePage;

import java.time.Duration;

@Test(groups = {"carousel"})
public class CarouselTest {
    private WebDriver driver;
    private PropertyPage propertyPage;
    private CarouselHelper carouselHelper;
    private SafariNowHomePage safariNowHomePage;

    @BeforeClass(groups = {"carousel"})
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        propertyPage = new PropertyPage(driver);
        carouselHelper = new CarouselHelper(driver);
        safariNowHomePage = new SafariNowHomePage(driver);
    }

    @Test
    public void testCarouselExistsOnHomePage() {
        driver.get("https://www.safarinow.com/"); //Start on the home page
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.dealsOffersCarouselContainer, safariNowHomePage.dealsOffersCarouselItems), "Deals and offers Swiper carousel should be present on the homepage.");
    }

    @Test
    public void testClickNextOnHomePage() {
        driver.get("https://www.safarinow.com/"); //Start on the home page
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.dealsOffersNextButton, safariNowHomePage.dealsOffersCarouselItems), "The image did not change when clicking next.");
    }

    @Test
    public void testClickPreviousOnHomePage() {
        driver.get("https://www.safarinow.com/"); //Start on the home page
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.dealsOffersPrevButton, safariNowHomePage.dealsOffersCarouselItems), "The image did not change when clicking previous.");
    }

    @Test
    public void testMoveThroughCarouselNext() {
        driver.get("https://www.safarinow.com/"); //Start on the home page
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.dealsOffersNextButton, safariNowHomePage.dealsOffersCarouselItems, "next"), "Carousel did not move through all the images when clicking next");
    }

    @Test
    public void testMoveThroughCarouselPrevious() {
        driver.get("https://www.safarinow.com/"); //Start on the home page
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.dealsOffersPrevButton, safariNowHomePage.dealsOffersCarouselItems, "previous"), "Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testPopularDestinationsCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.popularDestinationsCarouselContainer, safariNowHomePage.popularDestinationsCarouselItems), "Popular Destinations carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.popularDestinationsNextButton, safariNowHomePage.popularDestinationsCarouselItems), "Popular Destinations image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.popularDestinationsPrevButton, safariNowHomePage.popularDestinationsCarouselItems), "Popular Destinations image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.popularDestinationsNextButton, safariNowHomePage.popularDestinationsCarouselItems, "next"), "Popular Destinations Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.popularDestinationsPrevButton, safariNowHomePage.popularDestinationsCarouselItems, "previous"), "Popular Destinations Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testWeekendGetawaysCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.weekendGetawaysCarouselContainer, safariNowHomePage.weekendGetawaysCarouselItems), "Weekend Getaways carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.weekendGetawaysNextButton, safariNowHomePage.weekendGetawaysCarouselItems), "Weekend Getaways image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.weekendGetawaysPrevButton, safariNowHomePage.weekendGetawaysCarouselItems), "Weekend Getaways image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.weekendGetawaysNextButton, safariNowHomePage.weekendGetawaysCarouselItems, "next"), "Weekend Getaways Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.weekendGetawaysPrevButton, safariNowHomePage.weekendGetawaysCarouselItems, "previous"), "Weekend Getaways Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testCityTripsCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.cityTripsCarouselContainer, safariNowHomePage.cityTripsCarouselItems), "City Trips carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.cityTripsNextButton, safariNowHomePage.cityTripsCarouselItems), "City Trips image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.cityTripsPrevButton, safariNowHomePage.cityTripsCarouselItems), "City Trips image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.cityTripsNextButton, safariNowHomePage.cityTripsCarouselItems, "next"), "City Trips Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.cityTripsPrevButton, safariNowHomePage.cityTripsCarouselItems, "previous"), "City Trips Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testKwazuluNatalBeachEscapesCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.kwazuluNatalBeachEscapesCarouselContainer, safariNowHomePage.kwazuluNatalBeachEscapesCarouselItems), "KwaZulu-Natal Beach Escapes carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.kwazuluNatalBeachEscapesNextButton, safariNowHomePage.kwazuluNatalBeachEscapesCarouselItems), "KwaZulu-Natal Beach Escapes image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.kwazuluNatalBeachEscapesPrevButton, safariNowHomePage.kwazuluNatalBeachEscapesCarouselItems), "KwaZulu-Natal Beach Escapes image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.kwazuluNatalBeachEscapesNextButton, safariNowHomePage.kwazuluNatalBeachEscapesCarouselItems, "next"), "KwaZulu-Natal Beach Escapes Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.kwazuluNatalBeachEscapesPrevButton, safariNowHomePage.kwazuluNatalBeachEscapesCarouselItems, "previous"), "KwaZulu-Natal Beach Escapes Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testWildlifeAdventuresCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.wildlifeAdventuresCarouselContainer, safariNowHomePage.wildlifeAdventuresCarouselItems), "Wildlife Adventures carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.wildlifeAdventuresNextButton, safariNowHomePage.wildlifeAdventuresCarouselItems), "Wildlife Adventures image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.wildlifeAdventuresPrevButton, safariNowHomePage.wildlifeAdventuresCarouselItems), "Wildlife Adventures image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.wildlifeAdventuresNextButton, safariNowHomePage.wildlifeAdventuresCarouselItems, "next"), "Wildlife Adventures Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.wildlifeAdventuresPrevButton, safariNowHomePage.wildlifeAdventuresCarouselItems, "previous"), "Wildlife Adventures Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testExploreTheGardenRouteCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.exploreTheGardenRouteCarouselContainer, safariNowHomePage.exploreTheGardenRouteCarouselItems), "Explore the Garden Route carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.exploreTheGardenRouteNextButton, safariNowHomePage.exploreTheGardenRouteCarouselItems), "Explore the Garden Route image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.exploreTheGardenRoutePrevButton, safariNowHomePage.exploreTheGardenRouteCarouselItems), "Explore the Garden Route image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.exploreTheGardenRouteNextButton, safariNowHomePage.exploreTheGardenRouteCarouselItems, "next"), "Explore the Garden Route Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.exploreTheGardenRoutePrevButton, safariNowHomePage.exploreTheGardenRouteCarouselItems, "previous"), "Explore the Garden Route Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testAmazingCountryEscapesCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.amazingCountryEscapesCarouselContainer, safariNowHomePage.amazingCountryEscapesCarouselItems), "Amazing Country Escapes carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.amazingCountryEscapesNextButton, safariNowHomePage.amazingCountryEscapesCarouselItems), "Amazing Country Escapes image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.amazingCountryEscapesPrevButton, safariNowHomePage.amazingCountryEscapesCarouselItems), "Amazing Country Escapes image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.amazingCountryEscapesNextButton, safariNowHomePage.amazingCountryEscapesCarouselItems, "next"), "Amazing Country Escapes Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.amazingCountryEscapesPrevButton, safariNowHomePage.amazingCountryEscapesCarouselItems, "previous"), "Amazing Country Escapes Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testWestCoastGetawaysCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.westCoastGetawaysCarouselContainer, safariNowHomePage.westCoastGetawaysCarouselItems), "West Coast Getaways carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.westCoastGetawaysNextButton, safariNowHomePage.westCoastGetawaysCarouselItems), "West Coast Getaways image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.westCoastGetawaysPrevButton, safariNowHomePage.westCoastGetawaysCarouselItems), "West Coast Getaways image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.westCoastGetawaysNextButton, safariNowHomePage.westCoastGetawaysCarouselItems, "next"), "West Coast Getaways Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.westCoastGetawaysPrevButton, safariNowHomePage.westCoastGetawaysCarouselItems, "previous"), "West Coast Getaways Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testDiscoverTheWinelandsCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.discoverTheWinelandsCarouselContainer, safariNowHomePage.discoverTheWinelandsCarouselItems), "Discover the Winelands carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.discoverTheWinelandsNextButton, safariNowHomePage.discoverTheWinelandsCarouselItems), "Discover the Winelands image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.discoverTheWinelandsPrevButton, safariNowHomePage.discoverTheWinelandsCarouselItems), "Discover the Winelands image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.discoverTheWinelandsNextButton, safariNowHomePage.discoverTheWinelandsCarouselItems, "next"), "Discover the Winelands Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.discoverTheWinelandsPrevButton, safariNowHomePage.discoverTheWinelandsCarouselItems, "previous"), "Discover the Winelands Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testIncredibleRoadTripsCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.incredibleRoadTripsCarouselContainer, safariNowHomePage.incredibleRoadTripsCarouselItems), "Incredible Road Trips carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.incredibleRoadTripsNextButton, safariNowHomePage.incredibleRoadTripsCarouselItems), "Incredible Road Trips image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.incredibleRoadTripsPrevButton, safariNowHomePage.incredibleRoadTripsCarouselItems), "Incredible Road Trips image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.incredibleRoadTripsNextButton, safariNowHomePage.incredibleRoadTripsCarouselItems, "next"), "Incredible Road Trips Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.incredibleRoadTripsPrevButton, safariNowHomePage.incredibleRoadTripsCarouselItems, "previous"), "Incredible Road Trips Carousel did not move through all the images when clicking previous");
    }

    @Test
    public void testStopOversCarousel() {
        driver.get("https://www.safarinow.com/");
        Assert.assertTrue(carouselHelper.isCarouselPresent(safariNowHomePage.stopOversCarouselContainer, safariNowHomePage.stopOversCarouselItems), "Stop Overs carousel should be present.");
        Assert.assertTrue(carouselHelper.areImagesChanging(safariNowHomePage.stopOversNextButton, safariNowHomePage.stopOversCarouselItems), "Stop Overs image did not change when clicking next.");
        Assert.assertTrue(carouselHelper.areImagesChangingBackwards(safariNowHomePage.stopOversPrevButton, safariNowHomePage.stopOversCarouselItems), "Stop Overs image did not change when clicking previous.");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.stopOversNextButton, safariNowHomePage.stopOversCarouselItems, "next"), "Stop Overs Carousel did not move through all the images when clicking next");
        Assert.assertTrue(carouselHelper.moveThroughCarousel(safariNowHomePage.stopOversPrevButton, safariNowHomePage.stopOversCarouselItems, "previous"), "Stop Overs Carousel did not move through all the images when clicking previous");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}