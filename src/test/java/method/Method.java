package method;

import configs.dataPaths;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pageObjects.FlightPage;
import pageObjects.HomePage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static configs.dataPaths.dataPath;
import static configs.dataPaths.screenshotFolder;
import static io.restassured.RestAssured.given;
import static testMethods.TSMethods.*;
import static testMethods.TSMethods.excelUtils;

public class Method {
	public static WebDriver driver;

	public String readEnvironmentVariable(String variableIdentifier){

		String environmentVariableValue;

		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(dataPaths.dataBasePath + "\\src\\test\\resources\\configFiles\\")
					.filename("environmentFiles.env")
					.load();
			environmentVariableValue = dotenv.get(variableIdentifier);


			if (environmentVariableValue == null) {
				System.out.println(variableIdentifier+" not found.");
				throw new IllegalArgumentException(variableIdentifier+ " not found.");
			}
			return environmentVariableValue;

		} catch (Exception e) {
			System.out.println("Error loading environment variables: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to retrieve required environment variable "+ variableIdentifier+".");
		}

	}

	public String getBearerToken (String environment){

		String bearerToken = "";

		if(environment.equalsIgnoreCase("LIVE")) {
			bearerToken = this.readEnvironmentVariable("LIVE_BEARER_TOKEN");
		}
		else if(environment.equalsIgnoreCase("PREPROD")) {
			bearerToken = this.readEnvironmentVariable("PREPROD_BEARER_TOKEN");
		}
		else if(environment.equalsIgnoreCase("BETA")) {
			bearerToken = this.readEnvironmentVariable("BETA_BEARER_TOKEN");
		}
		else if(environment.equalsIgnoreCase("ALPHA")) {
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
		File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

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

	public String getCID(WebDriver driver){
		String correlationId = "";

		String URL = driver.getCurrentUrl();
		String [] brokenURL = URL.split("&");
		brokenURL = URL.split("correlation_id=");
		try {
			correlationId = brokenURL[1];
		} catch (ArrayIndexOutOfBoundsException e){
			correlationId = "NA";
		}

        return correlationId;
    }

	public String getConsole(WebDriver driver){
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


	public String scientificNotationToString(String value){
		String correctedValue = String.format("%.0f", value);
        return correctedValue;
    }

	public void departureMonthSelector (WebDriver driver, String departureMonth) throws InterruptedException {

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

		driver.findElement(HomePage.departureDate).click();

		Thread.sleep(500);

		selectedMonth  =selectedMonthArr[0];

		boolean isCalenderPrefilled = false;

		if (!selectedMonth.equalsIgnoreCase(currentMonthInString)){

			isCalenderPrefilled = true;

		}

		currentMonthInString = selectedMonth;

		Thread.sleep(2000);


		int numberOfMonthsDifference = monthMap.get(departureMonth.toUpperCase()) - monthMap.get(currentMonthInString.toUpperCase());

		if (numberOfMonthsDifference > 0){
		for (int a = 0; a < numberOfMonthsDifference; a++) {
			Thread.sleep(200);

			driver.findElement(HomePage.nextMonth).click();
		}} else if (numberOfMonthsDifference < 0) {

			if (isCalenderPrefilled){

				for (int a = 0; a < numberOfMonthsDifference; a++) {
					Thread.sleep(200);

					driver.findElement(By.xpath("(//button[@title='Previous month'])[1]")).click();
				}

			}

			numberOfMonthsDifference = (12 - monthMap.get(currentMonthInString.toUpperCase()));
			numberOfMonthsDifference = (monthMap.get(departureMonth.toUpperCase())) + numberOfMonthsDifference;

			for (int a = 0; a < numberOfMonthsDifference; a++) {
				Thread.sleep(200);

				driver.findElement(HomePage.nextMonth).click();

			}
		}
	}

	public void returnMonthSelector (WebDriver driver, String departureMonth, String returnMonth) throws InterruptedException {

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

		if (numberOfMonthsDifference > 0){
			for (int a = 0; a < numberOfMonthsDifference; a++) {
				Thread.sleep(200);

				driver.findElement(HomePage.nextMonth).click();
			}} else if (numberOfMonthsDifference < 0) {
			numberOfMonthsDifference = (12 - monthMap.get(departureMonth.toUpperCase()));
			numberOfMonthsDifference = (monthMap.get(returnMonth.toUpperCase())) + numberOfMonthsDifference;

			for (int a = 0; a < numberOfMonthsDifference; a++) {
				Thread.sleep(200);

				driver.findElement(HomePage.nextMonth).click();

			}
		}
	}

	public void paxSender(WebDriver driver, String adultCount, String youngAdultCount, String childCount, String infantCount, String airline1, String airline2) throws IOException, InterruptedException {

		List<Object[]> pax = new ArrayList<>();

		// Extracting all test data from test cases in test data sheet
		int adultPaxCount = Integer.parseInt(adultCount)-1;
		int youngAdultPaxCount = Integer.parseInt(youngAdultCount);
		int childPaxCount = Integer.parseInt(childCount);
		int infantPaxCount = Integer.parseInt(infantCount);


		//Adding adults to list
		for (int i = 3; i < adultPaxCount + 3; i++) {
			String paxType = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 0);
			String title = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 1);
			String firstName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 2);
			String middleName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 3);
			String lastName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 4);
			String dateOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 5);
			String monthOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 6);
			String yearOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 7);
			String passportNumber = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 8);
			String passportExpiryDate = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 9);
			String passportExpiryMonth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 10);
			String passportExpiryYear = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 11);
			String passportNationality = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 12);
			String passportIssuingCountry = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 13);
			String addBaggage = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 14);

			pax.add(new Object[]{
					paxType,
					title,
					firstName,
					middleName,
					lastName,
					dateOfBirth,
					monthOfBirth,
					yearOfBirth,
					passportNumber,
					passportExpiryDate,
					passportExpiryMonth,
					passportExpiryYear,
					passportNationality,
					passportIssuingCountry,
					addBaggage});
		}


		//Adding young adults to list
		for (int i = 12; i < youngAdultPaxCount + 12; i++) {
			String paxType = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 0);
			String title = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 1);
			String firstName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 2);
			String middleName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 3);
			String lastName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 4);
			String dateOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 5);
			String monthOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 6);
			String yearOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 7);
			String passportNumber = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 8);
			String passportExpiryDate = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 9);
			String passportExpiryMonth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 10);
			String passportExpiryYear = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 11);
			String passportNationality = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 12);
			String passportIssuingCountry = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 13);
			String addBaggage = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 14);

			pax.add(new Object[]{
					paxType,
					title,
					firstName,
					middleName,
					lastName,
					dateOfBirth,
					monthOfBirth,
					yearOfBirth,
					passportNumber,
					passportExpiryDate,
					passportExpiryMonth,
					passportExpiryYear,
					passportNationality,
					passportIssuingCountry,
					addBaggage});
		}


		// Adding child to list
		for (int i = 21; i < childPaxCount + 21; i++) {
			String paxType = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 0);
			String title = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 1);
			String firstName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 2);
			String middleName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 3);
			String lastName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 4);
			String dateOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 5);
			String monthOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 6);
			String yearOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 7);
			String passportNumber = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 8);
			String passportExpiryDate = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 9);
			String passportExpiryMonth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 10);
			String passportExpiryYear = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 11);
			String passportNationality = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 12);
			String passportIssuingCountry = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 13);
			String addBaggage = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 14);

			pax.add(new Object[]{
					paxType,
					title,
					firstName,
					middleName,
					lastName,
					dateOfBirth,
					monthOfBirth,
					yearOfBirth,
					passportNumber,
					passportExpiryDate,
					passportExpiryMonth,
					passportExpiryYear,
					passportNationality,
					passportIssuingCountry,
					addBaggage});
		}

		//Adding infants to pax
		// Adding child to list
		for (int i = 30; i < infantPaxCount + 30; i++) {
			String paxType = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 0);
			String title = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 1);
			String firstName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 2);
			String middleName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 3);
			String lastName = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 4);
			String dateOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 5);
			String monthOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 6);
			String yearOfBirth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 7);
			String passportNumber = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 8);
			String passportExpiryDate = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 9);
			String passportExpiryMonth = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 10);
			String passportExpiryYear = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 11);
			String passportNationality = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 12);
			String passportIssuingCountry = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 13);
			String addBaggage = excelUtils.readDataFromExcel(dataPath, "Passenger details", i, 14);

			pax.add(new Object[]{
					paxType,
					title,
					firstName,
					middleName,
					lastName,
					dateOfBirth,
					monthOfBirth,
					yearOfBirth,
					passportNumber,
					passportExpiryDate,
					passportExpiryMonth,
					passportExpiryYear,
					passportNationality,
					passportIssuingCountry,
					addBaggage});
		}

		FlightPage enterPaxDetails = new FlightPage(driver);

			for (int i = 0; i < pax.size(); i++) {

				// Select title
				for (int j = 0; j <= 1; j++){
					// Select title
					String title = (String) pax.get(i)[j];
					enterPaxDetails.titleSelector(i,title);
				}

				// Enter First name
				for (int j = 2; j <= 2; j++){
					String firstName = (String) pax.get(i)[j];
					enterPaxDetails.inputFirstName(i,firstName);
				}

				// Enter Last name
				for (int j = 4; j <= 4; j++){
					String lastName = "";
					if (airline1.equalsIgnoreCase("FA") && (airline2.isEmpty()||airline2.isBlank())){
						lastName = "Test";
					}
					else if (airline1.equalsIgnoreCase("FA") && airline2.equalsIgnoreCase("FA")) {
						lastName = "Test";
					}else {
						lastName = (String) pax.get(i)[j];
					}
					enterPaxDetails.inputLastName(i,lastName);
				}

				// Select date of birth
				for (int j = 5; j <= 5; j++){
					String dateOfBirth = (String) pax.get(i)[j];
					enterPaxDetails.selectDateOfBirth(i+1, dateOfBirth);

				}

				// Select month of birth
				for (int j = 6; j <= 6; j++){
					String monthOfBirth = (String) pax.get(i)[j];
					enterPaxDetails.selectMonthOfBirth(i+1, monthOfBirth);
				}

				// Select year of birth
				for (int j = 7; j <= 7; j++){
					String yearOfBirth = (String) pax.get(i)[j];
					enterPaxDetails.selectYearOfBirth(i+1, yearOfBirth);
				}

				// Input passport details
				WebElement passPortInfo = null;
				try{
					passPortInfo = driver.findElement(FlightPage.ppInfo);
				} catch (NoSuchElementException e){
					
				}
                try{
                if (passPortInfo.isDisplayed()){

					// Input passport number
					for (int j = 8; j <= 8; j++){
						String passPortNumber = (String) pax.get(i)[j];
						enterPaxDetails.enterPassportNumber(i, passPortNumber);
					}

					// Select date of passport expiry
					for (int j = 9; j <= 9; j++){
						String ppExpiryDate = (String) pax.get(i)[j];
						enterPaxDetails.selectDateOfppExpiry(i+1, ppExpiryDate);
					}

					// Select month of passport expiry
					for (int j = 10; j <= 10; j++){
						String monthOfppExpiry = (String) pax.get(i)[j];
						enterPaxDetails.selectMonthOfppExpiry(i+1, monthOfppExpiry);
					}

					// Select year of passport expiry
					for (int j = 11; j <= 11; j++){
						String yearOfppExpiry = (String) pax.get(i)[j];
						enterPaxDetails.selectYearppExpiry(i+1, yearOfppExpiry);
					}

					//Selecting passport Nationality
					for (int j = 12; j <= 12; j++){
						String ppNationality = (String) pax.get(i)[j];
						enterPaxDetails.selectppNationality(i, ppNationality);
					}

					//Selecting passport Nationality
					for (int j = 13; j <= 13; j++){
						String ppCountry = (String) pax.get(i)[j];
						enterPaxDetails.selectppIssuingCountry(i, ppCountry);
					}

				}}catch (NullPointerException e){
					
				}

			}



	}

	public String getCurrentTime(){
		// Get the current time
		LocalTime currentTime = LocalTime.now();

		// Define a format for the time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		// Format the current time as a string
		String currentTimeString = currentTime.format(formatter);
        return currentTimeString;
    }


	public void selectFromDropDown(WebDriver driver, WebElement dropdownElement, String value) throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, Duration.of(5, ChronoUnit.SECONDS));

		try{


			dropdownElement.click();

			Thread.sleep(1000);


		}catch (NoSuchElementException e){

		}

		try {



			wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//span[@class='mat-option-text'][text()='" + value + "']"))));

			driver.findElement(By.xpath("//span[@class='mat-option-text'][text()='" + value + "']")).click();
		}
		catch (TimeoutException | NoSuchElementException e){

			System.out.println("Desired value not available in dropdown");

		}

		}

		public String[] getPNR(String environment, String bookingReference) throws IOException {
			// Construct the API endpoint with the booking reference

			
			List<String> pnrs = null;
			pnrs = new ArrayList<>();

			String apiUrl = "";
			if (environment.equalsIgnoreCase("Preprod")) {
				apiUrl = "https://preprod-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			} else if (environment.equalsIgnoreCase("live")) {
				apiUrl = "https://wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			} else if (environment.equalsIgnoreCase("beta")) {
				apiUrl = "https://beta-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			}
			String pnrReference = "";
			// Bearer token for authorization
			String bearerToken = this.getBearerToken(environment);

			// Create an HttpClient instance
			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet request = new HttpGet(apiUrl);

			// Add the Authorization header with the bearer token
			request.addHeader("Authorization", "Bearer " + bearerToken);

			// Send the GET request and get the response
			HttpResponse response = httpClient.execute(request);


			// Check the response status code to determine if the cancellation was successful
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Booking information retrieved for the booking reference : "+ bookingReference);

				// Parse the response body
				HttpEntity entity = response.getEntity();
				String responseBody = EntityUtils.toString(entity);
				JSONObject jsonResponse = new JSONObject(responseBody);

				// Extract the booking status
				JSONObject statusObject = jsonResponse.getJSONObject("status");
				String bookingStatus = statusObject.getString("booking");

				// If status is "CANCELLED", ignore cancellation
				if (bookingStatus.equalsIgnoreCase("CANCELLED")) {

					System.out.println("Booking is already cancelled.");

				} else {
					// If status is not "CANCELLED"m get booking ID
					try {
						JSONObject jsonObject = new JSONObject(jsonResponse.toString());
						JSONArray reservationArray = jsonObject.getJSONArray("reservations");

						for (int i = 0; i < reservationArray.length(); i++) {
							JSONObject reservation = reservationArray.getJSONObject(i);
							pnrReference = reservation.getString("pnrReference");

							System.out.println("Reservation " + (i + 1) + ":");
							System.out.println("PNR Reference: " + pnrReference);
							System.out.println();
							pnrs.add(pnrReference);

						}
					} catch (JSONException e) {
						
					}
				}
			} else {
				System.out.println("Booking information retrival failed. Status code: " + statusCode);
				// Print response body for debugging
				HttpEntity entity = response.getEntity();
				String responseBody = EntityUtils.toString(entity);
				System.out.println("Response body: " + responseBody);
				// Handle errors or exceptions
			}
			return pnrs.toArray(new String[0]);
        }

	public void cancelBooking(String environment, String bookingReference) throws IOException {
		String[] pnrs = getPNR(environment, bookingReference);

		// If PNRs are retrieved, attempt to cancel each booking
		for (String pnr : pnrs) {
			cancelBookingRequest(environment, bookingReference, pnr);
		}
	}


	private void cancelBookingRequest(String environment, String bookingReference, String pnr) throws IOException {

		String BASE_URL_PREPROD = "https://preprod-wapi.travelstart.com/website-services/api/itinerary/cancel";
		String BASE_URL_BETA = "https://beta-wapi.travelstart.com/website-services/api/itinerary/cancel";
		String BASE_URL_LIVE = "https://wapi.travelstart.com/website-services/api/itinerary/cancel";

		String BEARER_TOKEN = getBearerToken(environment);

		// Construct the URL for cancellation based on environment
		String apiUrl = "";
		String cancelUrl = "";
		if (environment.equalsIgnoreCase("Beta")) {
			cancelUrl = BASE_URL_BETA + "/" + bookingReference + "/" + pnr;
		} else if (environment.equalsIgnoreCase("Preprod")) {
			cancelUrl = BASE_URL_PREPROD + "/" + bookingReference + "/" + pnr;
		} else if (environment.equalsIgnoreCase("Live")) {
			cancelUrl = BASE_URL_LIVE + "/" + bookingReference + "/" + pnr;
		}

		// Create an HttpClient instance
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Create a PUT request to cancel the booking
		HttpPut request = new HttpPut(cancelUrl);

		// Add the Authorization header with the bearer token
		request.addHeader("Authorization", "Bearer " + BEARER_TOKEN);

		// Send the PUT request and get the response
		HttpResponse response = httpClient.execute(request);

		// Check the response status code to determine if the cancellation was successful
		int statusCode = response.getStatusLine().getStatusCode();

		// Parse the response body
		HttpEntity entity = response.getEntity();
		String responseBody = EntityUtils.toString(entity);
		JSONObject jsonResponse = new JSONObject(responseBody.toString());


		// Extract the booking status
		JSONObject statusObject = jsonResponse.getJSONObject("result");
		boolean bookingCancelStatus = statusObject.getBoolean("successful");

		if (statusCode == 200 && bookingCancelStatus) {
			System.out.println("Booking with Booking Reference: " + bookingReference + " and PNR: " + pnr + " cancelled successfully.");
		} else if (statusCode == 200 && !bookingCancelStatus) {
			response = httpClient.execute(request);
			// Parse the response body
			entity = response.getEntity();
			responseBody = EntityUtils.toString(entity);
			jsonResponse = new JSONObject(responseBody.toString());

			// Extract the booking status
			statusObject = jsonResponse.getJSONObject("result");
			bookingCancelStatus = statusObject.getBoolean("successful");
		}

		if (!bookingCancelStatus){
			System.out.println("Cancellation failed for "+ bookingReference +" with PNR "+ pnr);
		}

		// Close the HttpClient
		httpClient.close();
	}

	public boolean checkSubscriptionStatus(String username, String password){
		boolean subscriptionStatus = false;
		String requestBody = "{\n" +
				"    \"password\": \"" + password + "\",\n" +
				"    \"provider\": \"travelstart\",\n" +
				"    \"token\": null,\n" +
				"    \"userAgent\": {\n" +
				"        \"language\": \"en\",\n" +
				"        \"market\": \"za\"\n" +
				"    },\n" +
				"    \"username\": \"" + username + "\"\n" +
				"}";

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("https://preprod-tsacc.travelstart.com/api/v3/login");

		Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200");

		String responseBody = "";

		if (response.getStatusCode() == 200){

			responseBody = response.getBody().asString();

		}


		if (responseBody.contains("\"isTSPlusSubscriber\":true")){

			subscriptionStatus = true;

		}

        return subscriptionStatus;
    }

	public boolean checkAccountStatus(String username, String password){
		boolean accountActive = false;
		String requestBody = "{\n" +
				"    \"password\": \"" + password + "\",\n" +
				"    \"provider\": \"travelstart\",\n" +
				"    \"token\": null,\n" +
				"    \"userAgent\": {\n" +
				"        \"language\": \"en\",\n" +
				"        \"market\": \"za\"\n" +
				"    },\n" +
				"    \"username\": \"" + username + "\"\n" +
				"}";

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("https://preprod-tsacc.travelstart.com/api/v3/login");

		Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200");

		String responseBody = "";

		if (response.getStatusCode() == 200){

			responseBody = response.getBody().asString();


		}

		if (responseBody.contains("ACTIVE")){

			accountActive = true;

		}

		return accountActive;

	}



	public static String generateCID(){

		String CID = Method.generateRandomFileName();

		int length = CID.length();
		CID = CID.substring(length / 2);
		CID = "automation"+CID;

        return CID;



    }



	public WebDriver launchBrowser( WebDriver driver, String browser){

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


	public boolean isBundled(String domain, String tripType, String origin, String destination){

		boolean isBundled = true;
		boolean isOriginDomestic = false;
		boolean isDestinationDomestic = false;

		String ZAAirports[] = {"JNB","CPT","DUR","GRG","PLZ","UTN","ELS","AGZ","ALJ","ADY","BFN","AAM"};
		String NGAirports[] = {"ABV","LOS","AKR","ABB","BCU","BNI","CBQ","ENU","GMO","IBA","ILR","JOS"};
        //param =  origin

		if (domain.equalsIgnoreCase("ZA")){
			
			for (int i = 0; i < ZAAirports.length; i++){
				
				String airport = ZAAirports[i];
				
				if (airport.equalsIgnoreCase(origin)){

					isOriginDomestic = true;
					break;

				} else {
					isOriginDomestic = false;
				}
				
			}

			for (int i = 0; i < ZAAirports.length; i++){

				String airport = ZAAirports[i];

				if (airport.equalsIgnoreCase(destination)){

					isDestinationDomestic = true;
					break;

				} else {

					isDestinationDomestic = false;

				}

			}

		} else if (domain.equalsIgnoreCase("NG")) {

			for (int i = 0; i < NGAirports.length; i++){

				String airport = NGAirports[i];

				if (airport.equalsIgnoreCase(origin)){
					isOriginDomestic = true;
				} else {
					isOriginDomestic = false;
				}

				if (isOriginDomestic) break;

			}

			for (int i = 0; i < NGAirports.length; i++){

				String airport = NGAirports[i];

				if (airport.equalsIgnoreCase(origin)){
					isDestinationDomestic = true;
				} else {
					isDestinationDomestic = false;
				}

				if (isDestinationDomestic) break;

			}
		}


		if (tripType.equalsIgnoreCase("Oneway")) {

			isBundled = true;

		}

		else if (tripType.equalsIgnoreCase("Return")){

			if (isOriginDomestic && isDestinationDomestic){

				isBundled = false;

			}else{

				isBundled = true;
			}


		}


        return isBundled;
    }


	public String getBaseURL (String environment, String domain, String cpy_source) throws IOException {

		String baseURL = "";

		String urlPath = dataPaths.URLs;

		environment = environment.toUpperCase();

		// Setting up URL for ZA domain
		if (domain.equalsIgnoreCase("ZA")){



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

		if (cpy_source.equalsIgnoreCase("tszaweb")|| cpy_source.equalsIgnoreCase("tsngweb")|| cpy_source.isEmpty() || cpy_source.isBlank() || cpy_source.equals("-")){

			baseURL = baseURL;

		}
		else {
			baseURL = baseURL+"?cpysource="+cpy_source;
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

		}
		catch (NoSuchElementException | TimeoutException e) {

		}

		// Initializing boolean variable to asser flight details page
		boolean istargetElementLoaded = false;

		try{

			// Assigning boolean value to assertion variable if flight details page is available
			istargetElementLoaded = targetElement.isDisplayed();

		}catch (NullPointerException e){


		}


		return istargetElementLoaded;
	}


	public boolean verifyRedirection(WebDriver driver, By targetElementLocator, String targetElementName){

		// Asserting Traveller Page
		WebElement targetElement = null;

		long duration = 75;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(duration));


		try {

			// Waits until flight details page is loaded for maximum 60 seconds
			wait.until(ExpectedConditions.visibilityOfElementLocated(targetElementLocator));
			targetElement = driver.findElement(targetElementLocator);

		}
		catch (NoSuchElementException | TimeoutException e) {

		}

		// Initializing boolean variable to asser flight details page
		boolean istargetElementLoaded = false;

		try{

			// Assigning boolean value to assertion variable if flight details page is available
			istargetElementLoaded = targetElement.isDisplayed();

		}catch (NullPointerException e){


		}


		return istargetElementLoaded;



	}

	public boolean verifyRedirection(WebDriver driver, By desiredElementLocator, By errorElementLocator) throws InterruptedException {

		boolean isFlowWorking = false;
		boolean errorOccured = false;

		WebElement desiredElement;

		WebElement errorElement;

		int wait = 3000;

		for (int i = 0; i < 25; i++){

			Thread.sleep(wait);

			try{

				desiredElement = driver.findElement(desiredElementLocator);

				if (desiredElement.isDisplayed() && desiredElement.isEnabled()){

					isFlowWorking = true;

				}

			}
			catch (NoSuchElementException | NullPointerException e){

				try{

					errorElement = driver.findElement(errorElementLocator);

					if (errorElement.isDisplayed()){

						isFlowWorking = false;
						errorOccured = true;

					}

				}catch (NoSuchElementException | NullPointerException e1){


				}

			}

			if (isFlowWorking) break;

			if (errorOccured) break;

		}


        return isFlowWorking;
    }


	public String getUUID(String environment, String bookingReference) throws IOException {

		String UUID = "";

		String apiUrl = "";

		if (environment.equalsIgnoreCase("Preprod")) {
			apiUrl = "https://preprod-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		} else if (environment.equalsIgnoreCase("live")) {
			apiUrl = "https://wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		} else if (environment.equalsIgnoreCase("beta")) {
			apiUrl = "https://beta-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		}

		// Bearer token for authorization
		String bearerToken = this.getBearerToken(environment);

		// Create an HttpClient instance
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet request = new HttpGet(apiUrl);

		// Add the Authorization header with the bearer token
		request.addHeader("Authorization", "Bearer " + bearerToken);

		// Send the GET request and get the response
		HttpResponse response = httpClient.execute(request);


		// Check the response status code to determine if the cancellation was successful
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == 200) {

			// Parse the response body

			HttpEntity entity = response.getEntity();

			String responseBody = EntityUtils.toString(entity);

			JSONObject jsonResponse = new JSONObject(responseBody);

			UUID = jsonResponse.getString("uuid");


		}else {

			System.out.println("UUID Retrieval failed. Itinerary API returned status code: "+ statusCode);

		}


			return UUID;
    }

	public String getCpySource(String environment, String bookingReference) throws IOException {
		String cpySource = "";

		JSONObject bookingSummaryResponse = this.getBookingSummaryResponse(environment, bookingReference);
		cpySource = bookingSummaryResponse.getString("companyCode");




        return cpySource;
    }

	public JSONObject getBookingSummaryResponse(String environment, String bookingReference) throws IOException {

		JSONObject bookingSummaryResponse = null;

			String apiUrl = "";

			if (environment.equalsIgnoreCase("Preprod")) {
				apiUrl = "https://preprod-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			} else if (environment.equalsIgnoreCase("live")) {
				apiUrl = "https://wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			} else if (environment.equalsIgnoreCase("beta")) {
				apiUrl = "https://beta-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
			}

			// Bearer token for authorization
		String bearerToken = this.getBearerToken(environment);

			// Create an HttpClient instance
			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet request = new HttpGet(apiUrl);

			// Add the Authorization header with the bearer token
			request.addHeader("Authorization", "Bearer " + bearerToken);

			// Send the GET request and get the response
			HttpResponse response = httpClient.execute(request);


			// Check the response status code to determine if the cancellation was successful
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				// Parse the response body

				HttpEntity entity = response.getEntity();

				String responseBody = EntityUtils.toString(entity);

				bookingSummaryResponse = new JSONObject(responseBody);


			}else {

				System.out.println("Booking response Retrieval failed. Itinerary API returned status code: "+ statusCode);

			}

			return bookingSummaryResponse;


	}

	public String getInvID(String environment, String bookingReference) throws IOException {

		String invID = "";

		String apiUrl = "";

		if (environment.equalsIgnoreCase("Preprod")) {
			apiUrl = "https://preprod-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		} else if (environment.equalsIgnoreCase("live")) {
			apiUrl = "https://wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		} else if (environment.equalsIgnoreCase("beta")) {
			apiUrl = "https://beta-wapi.travelstart.com/website-services/api/itinerary/summary/" + bookingReference;
		}

		// Bearer token for authorization
		String bearerToken = this.getBearerToken(environment);

		// Create an HttpClient instance
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet request = new HttpGet(apiUrl);

		// Add the Authorization header with the bearer token
		request.addHeader("Authorization", "Bearer " + bearerToken);

		// Send the GET request and get the response
		HttpResponse response = httpClient.execute(request);


		// Check the response status code to determine if the cancellation was successful
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == 200) {

			// Parse the response body

			HttpEntity entity = response.getEntity();

			String responseBody = EntityUtils.toString(entity);

			JSONObject jsonResponse = new JSONObject(responseBody);

			JSONArray invoiceListArray = jsonResponse.getJSONArray("invoiceList");

			JSONObject invoiceListBody = invoiceListArray.getJSONObject(0);

			invID = String.valueOf(invoiceListBody.getBigInteger("invoiceId"));



		}else {

			System.out.println("UUID Retrieval failed. Itinerary API returned status code: "+ statusCode);

		}

        return invID;
    }



	public int stringToInteger(String value){

		// Remove all characters except numbers and decimal point
		String cleanedStr = value.replaceAll("[^\\d.]", "");

		// Check if the string contains a decimal point
		if (cleanedStr.contains(".")) {
			// Handle it as a double and convert to integer
			return (int) Double.parseDouble(cleanedStr);
		} else {
			// Handle normal integer case
			return Integer.parseInt(cleanedStr);
		}

    }

	public String removeAlphaSpecialAndSpaceFromString(String input){

		// Replace all non-digit characters with an empty string
		String result = input.replaceAll("\\D+", "");


        return result;
    }

	public Map<String, Integer> statusMap = new HashMap<>();

	// Constructor to initialize the map
	public Method() {
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

	public String getTimeStamp(){

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