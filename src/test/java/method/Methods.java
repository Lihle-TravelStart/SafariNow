package method;

import configs.dataPaths;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.PropertyPage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static configs.dataPaths.screenshotFolder;

@Slf4j
public class Methods {
    public static WebDriver driver;
    static ExcelUtils excelUtils = new ExcelUtils();

    public String readEnvironmentVariable(String variableIdentifier) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(dataPaths.dataBasePath + "/src/test/resources/configFiles/")
                    .filename("environmentFiles.env")
                    .load();
            String environmentVariableValue = dotenv.get(variableIdentifier);

            if (environmentVariableValue == null) {
                log.error("{} not found.", variableIdentifier);
                throw new IllegalArgumentException(variableIdentifier + " not found.");
            }
            return environmentVariableValue;
        } catch (Exception e) {
            log.error("Error loading environment variables: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve required environment variable " + variableIdentifier + ".", e);
        }
    }

    public String getBearerToken(String environment) {

        String bearerToken = "";

        if (environment.equalsIgnoreCase("LIVE")) {
            bearerToken = this.readEnvironmentVariable("LIVE_BEARER_TOKEN");
        } else if (environment.equalsIgnoreCase("PREPROD")) {
            bearerToken = this.readEnvironmentVariable("PREPROD_BEARER_TOKEN");
        } else if (environment.equalsIgnoreCase("BETA")) {
            bearerToken = this.readEnvironmentVariable("BETA_BEARER_TOKEN");
        } else if (environment.equalsIgnoreCase("ALPHA")) {
            bearerToken = this.readEnvironmentVariable("ALPHA_BEARER_TOKEN");
        }
        return bearerToken;
    }


    public static String ReadPropertyFile(String path, String key) throws Exception {
        FileReader f = new FileReader(path);
        Properties p = new Properties();
        p.load(f);
        return p.getProperty(key);
    }

    public void takeScreenshot(WebDriver driver) {
        // Generate a random file name
        String fileName = generateRandomFileName() + ".png";
        // Take screenshot
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Set the destination file
        File destinationFile = new File(screenshotFolder + File.separator + fileName);
        try {
            // Copy the screenshot to the destination file
            FileUtils.copyFile(screenshotFile, destinationFile);
            System.out.println("Screenshot saved as: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    private static String generateRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public String doubleToString(String number) {
        // Convert String to double
        double doubleValue = Double.parseDouble(number);
        // Convert double to int
        int intValue = (int) doubleValue;
        // Convert int to String
        String stringValue = String.valueOf(intValue);
        return stringValue;
    }

    public String getConsole(WebDriver driver) {
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        String logs = null;
        // Check for errors in console logs
        for (LogEntry entry : logEntries) {
            if (entry.getLevel().toString().equalsIgnoreCase("SEVERE")) {
                logs = ("Error found in console: " + entry.getMessage());
            }
        }
        return logs;
    }

    public int stringToInt(String str) {
        try {
            if (str == null) {
                return 0; // Return default value for null input
            }
            // Remove any non-numeric characters except digits and decimal point
            str = str.replaceAll("[^\\d.]", "");
            // Parse the string as a float, round it, and cast it to an int
            float doubleNumber = Float.parseFloat(str);
            return Math.round(doubleNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input does not contain a valid number: " + str, e);
        }
    }

    public String scientificNotationToString(String value) {
        String correctedValue = String.format("%.0f", value);
        return correctedValue;
    }

  /*  public void departureMonthSelector(WebDriver driver, String departureMonth) throws InterruptedException {

        // Define a map to map month numbers to month names
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("JANUARY", 1);
        monthMap.put("FEBRUARY", 2);
        monthMap.put("MARCH", 3);
        monthMap.put("APRIL", 4);
        monthMap.put("MAY", 5);
        monthMap.put("JUNE", 6);
        monthMap.put("JULY", 7);
        monthMap.put("AUGUST", 8);
        monthMap.put("SEPTEMBER", 9);
        monthMap.put("OCTOBER", 10);
        monthMap.put("NOVEMBER", 11);
        monthMap.put("DECEMBER", 12);

        // Get the current YearMonth
        YearMonth currentYearMonth = YearMonth.now();

        // Get the current month
        Month currentMonth = currentYearMonth.getMonth();

        String currentMonthInString = String.valueOf(currentMonth);

        String selectedMonth = driver.findElement(By.xpath("(//div[@class='ngb-dp-month-name ng-star-inserted'])[1]")).getText();
        String selectedMonthArr[] = selectedMonth.split(" ");

        driver.findElement(PropertyPage.departureDate).click();

        Thread.sleep(500);

        selectedMonth = selectedMonthArr[0];

        boolean isCalenderPrefilled = false;

        if (!selectedMonth.equalsIgnoreCase(currentMonthInString)) {

            isCalenderPrefilled = true;

        }

        currentMonthInString = selectedMonth;

        Thread.sleep(2000);

        int numberOfMonthsDifference = monthMap.get(departureMonth.toUpperCase()) - monthMap.get(currentMonthInString.toUpperCase());

        if (numberOfMonthsDifference > 0) {
            for (int a = 0; a < numberOfMonthsDifference; a++) {
                Thread.sleep(200);

                driver.findElement(PropertyPage.nextMonth).click();
            }
        } else if (numberOfMonthsDifference < 0) {

            if (isCalenderPrefilled) {

                for (int a = 0; a < numberOfMonthsDifference; a++) {
                    Thread.sleep(200);

                    driver.findElement(By.xpath("(//button[@title='Previous month'])[1]")).click();
                }

            }

            numberOfMonthsDifference = (12 - monthMap.get(currentMonthInString.toUpperCase()));
            numberOfMonthsDifference = (monthMap.get(departureMonth.toUpperCase())) + numberOfMonthsDifference;

            for (int a = 0; a < numberOfMonthsDifference; a++) {
                Thread.sleep(200);

                driver.findElement(PropertyPage.nextMonth).click();

            }
        }
    }

    public void returnMonthSelector(WebDriver driver, String departureMonth, String returnMonth) throws InterruptedException {

        // Define a map to map month numbers to month names
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("JANUARY", 1);
        monthMap.put("FEBRUARY", 2);
        monthMap.put("MARCH", 3);
        monthMap.put("APRIL", 4);
        monthMap.put("MAY", 5);
        monthMap.put("JUNE", 6);
        monthMap.put("JULY", 7);
        monthMap.put("AUGUST", 8);
        monthMap.put("SEPTEMBER", 9);
        monthMap.put("OCTOBER", 10);
        monthMap.put("NOVEMBER", 11);
        monthMap.put("DECEMBER", 12);

        int numberOfMonthsDifference = monthMap.get(returnMonth.toUpperCase()) - monthMap.get(departureMonth.toUpperCase());

        driver.findElement(By.xpath("//input[@id='arr_date0']")).click();

        if (numberOfMonthsDifference > 0) {
            for (int a = 0; a < numberOfMonthsDifference; a++) {
                Thread.sleep(200);

                driver.findElement(PropertyPage.nextMonth).click();
            }
        } else if (numberOfMonthsDifference < 0) {
            numberOfMonthsDifference = (12 - monthMap.get(departureMonth.toUpperCase()));
            numberOfMonthsDifference = (monthMap.get(returnMonth.toUpperCase())) + numberOfMonthsDifference;

            for (int a = 0; a < numberOfMonthsDifference; a++) {
                Thread.sleep(200);

                driver.findElement(PropertyPage.nextMonth).click();

            }
        }
    } */

    public String getCurrentTime() {
        // Get the current time
        LocalTime currentTime = LocalTime.now();

        // Define a format for the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the current time as a string
        String currentTimeString = currentTime.format(formatter);
        return currentTimeString;
    }

    public static String generateCID() {
        String CID = Methods.generateRandomFileName();
        int length = CID.length();
        CID = CID.substring(length / 2);
        CID = "automation" + CID;
        return CID;

    }

    public WebDriver launchBrowser(WebDriver driver, String browser) {

        if (browser.equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else if (browser.equalsIgnoreCase("Edge")) {
            driver = new EdgeDriver();
        }

        return driver;
    }

    public String getBaseURL(String environment, String domain, String cpy_source) throws IOException {
        String baseURL = "";
        String urlPath = dataPaths.URLs;
        environment = environment.toUpperCase();
        // Setting up URL for ZA domain
        if (domain.equalsIgnoreCase("ZA")) {
            switch (environment) {
                case "LIVE" -> baseURL = excelUtils.readDataFromExcel(urlPath, "URL's", 4, 1);
                case "BETA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 6, 1));
                case "PREPROD" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 8, 1));
                case "ALPHA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 10, 1));
                default -> System.out.println("Invalid environment name");
            }
        }
        // Setting up URL for NG domain
        else if (domain.equalsIgnoreCase("NG")) {
            switch (environment) {
                case "LIVE" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 5, 1));
                case "BETA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 7, 1));
                case "PREPROD" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 9, 1));
                case "ALPHA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 11, 1));
                default -> System.out.println("Invalid envinorment name");
            }
        }
        // Setting FS META
        else if (domain.equalsIgnoreCase("FS")) {
            switch (environment) {
                case "LIVE" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 12, 1));
                case "BETA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 13, 1));
                case "PREPROD" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 14, 1));
                case "ALPHA" -> baseURL = (excelUtils.readDataFromExcel(urlPath, "URL's", 15, 1));
                default -> System.out.println("Invalid envinorment name");
            }
        }

        if (cpy_source.equalsIgnoreCase("tszaweb") || cpy_source.equalsIgnoreCase("tsngweb") || cpy_source.isEmpty() || cpy_source.isBlank() || cpy_source.equals("-")) {
            baseURL = baseURL;
        } else {
            baseURL = baseURL + "?cpysource=" + cpy_source;
        }
        return baseURL;
    }

    public boolean verifyElementAvailability(By targetElementLocator, String targetElementName) {
        // Asserting Traveller Page
        WebElement targetElement = null;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            // Waits until flight details page is loaded for maximum 60 seconds
            wait.until(ExpectedConditions.visibilityOfElementLocated(targetElementLocator));
            targetElement = driver.findElement(targetElementLocator);
        } catch (NoSuchElementException | TimeoutException e) {
        }
        // Initializing boolean variable to asser flight details page
        boolean istargetElementLoaded = false;
        try {
            // Assigning boolean value to assertion variable if flight details page is available
            istargetElementLoaded = targetElement.isDisplayed();
        } catch (NullPointerException e) {
        }
        return istargetElementLoaded;
    }

    public boolean verifyRedirection(WebDriver driver, By targetElementLocator, String targetElementName) {
        // Asserting Traveller Page
        WebElement targetElement = null;
        long duration = 75;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
        try {
            // Waits until flight details page is loaded for maximum 60 seconds
            wait.until(ExpectedConditions.visibilityOfElementLocated(targetElementLocator));
            targetElement = driver.findElement(targetElementLocator);
        } catch (NoSuchElementException | TimeoutException e) {
        }
        // Initializing boolean variable to asser flight details page
        boolean istargetElementLoaded = false;
        try {
            // Assigning boolean value to assertion variable if flight details page is available
            istargetElementLoaded = targetElement.isDisplayed();
        } catch (NullPointerException e) {
        }
        return istargetElementLoaded;

    }

    public boolean verifyRedirection(WebDriver driver, By desiredElementLocator, By errorElementLocator) throws InterruptedException {

        boolean isFlowWorking = false;
        boolean errorOccured = false;

        WebElement desiredElement;

        WebElement errorElement;

        int wait = 3000;

        for (int i = 0; i < 25; i++) {

            Thread.sleep(wait);

            try {

                desiredElement = driver.findElement(desiredElementLocator);

                if (desiredElement.isDisplayed() && desiredElement.isEnabled()) {

                    isFlowWorking = true;

                }

            } catch (NoSuchElementException | NullPointerException e) {

                try {

                    errorElement = driver.findElement(errorElementLocator);

                    if (errorElement.isDisplayed()) {

                        isFlowWorking = false;
                        errorOccured = true;

                    }

                } catch (NoSuchElementException | NullPointerException e1) {
                }
            }
            if (isFlowWorking) break;
            if (errorOccured) break;
        }
        return isFlowWorking;
    }

    public Map<String, Integer> statusMap = new HashMap<>();

    // Constructor to initialize the map
    public Methods() {
        statusMap.put("PASSED", 0);
        statusMap.put("FAILED", 0);
        statusMap.put("SKIPPED", 0);
    }


    // Method to update and return the status map based on the provided status
    public Map<String, Integer> updateTestStatus(String status) {
        if (statusMap.containsKey(status)) {
            statusMap.put(status, statusMap.get(status) + 1);
        } else {
            System.out.println("Invalid status: " + status);
        }
        return statusMap;
    }


    public String removeSpecialCharacters(String input) {
        // Use regular expression to replace non-alphanumeric characters
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String retriveValueFromMap(Map<String, String> map, String searchKey) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().contains(searchKey)) {
                return entry.getValue();
            }
        }
        return null; // return null or a default value if no match is found
    }

    public String getTimeStamp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        return timestamp;
    }

    public List<String[]> getTestCasesFromTestCasesDocument(String testCasesDocumentPath, String testCaseSheetName) throws IOException {
        List<String[]> testCase = new ArrayList<>();
        // Extracting all test data from the specified test case sheet
        int testCasesCount = excelUtils.getRowCount(testCasesDocumentPath, testCaseSheetName);
        for (int i = 2; i < testCasesCount; i++) {
            String testCaseId = excelUtils.readDataFromExcel(testCasesDocumentPath, testCaseSheetName, i, 0);
            String testCaseSummary = excelUtils.readDataFromExcel(testCasesDocumentPath, testCaseSheetName, i, 2);
            String runTest = excelUtils.readDataFromExcel(testCasesDocumentPath, testCaseSheetName, i, 3);
            String[] testDetails = {testCaseId, testCaseSummary, runTest};
            testCase.add(testDetails);
        }
        return testCase;
    }

    public String getFlightNumbersFromDeeplink(String deepLink) {
        String flightNumbers = "";
        try {
            // Extract the part after "outbound_flight_number="
            flightNumbers = deepLink.split("outbound_flight_number=")[1];
            // Get the substring until the next '&' character, if present
            flightNumbers = flightNumbers.split("&")[0];
        } catch (Exception e) {
            // Return an informative error message instead of an empty string
            return "Flight number not found in the provided deeplink.";
        }
        return flightNumbers;
    }

    public List<Integer> pickRandomNumbers(int max, int count) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int randomNum = random.nextInt(max + 1); // Generates a number between 0 and max (inclusive)
            randomNumbers.add(randomNum);
        }
        return randomNumbers;
    }

    public boolean validateTwoListsMatching(List<String> listOne, List<String> listTwo) {

        // If the size of the two lists is different, return false immediately
        if (listOne.size() != listTwo.size()) {
            return false;
        }

        // Check if all seats in both lists are the same
        for (String seatSelected : listOne) {
            if (!listTwo.contains(seatSelected)) {
                return false;  // Return false if any seat is not found in the booking confirmation list
            }
        }

        // If all seats match, return true
        return true;
    }

}
