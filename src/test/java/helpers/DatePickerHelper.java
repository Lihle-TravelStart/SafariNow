package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DatePickerHelper {
    private WebDriver driver;

    private By monthYearDisplay = By.xpath("//div[@class='ui-datepicker-title']");
    private By nextButton = By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-e']");

    public DatePickerHelper(WebDriver driver) {
        this.driver = driver;
    }

    public void selectDateFromCalendar(String targetMonthYear, String day) {
        while (true) {
            String displayedMonthYear = driver.findElement(monthYearDisplay).getText();
            if (displayedMonthYear.equals(targetMonthYear)) {
                break; // Stop if the target month/year is displayed
            }
            driver.findElement(nextButton).click(); // Click next to navigate
        }

        // Select the correct day
        By dateLocator = By.xpath("//a[contains(@class, 'ui-state-default') and text()='" + day + "']");
        WebElement dateElement = driver.findElement(dateLocator);
        dateElement.click();
    }

    public void selectDate(String date) throws InterruptedException {
        String day = date.substring(8); // Extract the day (DD)
        String month = date.substring(5, 7); // Extract the month (MM)
        String year = date.substring(0, 4); // Extract the year (YYYY)

        String targetMonthYear = convertMonthYear(month, year);

        while (true) {
            String displayedMonthYear = driver.findElement(monthYearDisplay).getText();
            if (displayedMonthYear.equals(targetMonthYear)) {
                break; // Stop if the target month/year is displayed
            }
            driver.findElement(nextButton).click(); // Click next button to navigate months
            Thread.sleep(500); // Avoid rapid clicking
        }

        // Select the date
        By dateLocator = By.xpath("//a[contains(@class, 'ui-state-default') and text()='" + day + "']");
        WebElement dateElement = driver.findElement(dateLocator);
        dateElement.click();
    }

    private String convertMonthYear(String month, String year) {
        // Convert month number to full month name (e.g., "12" -> "December")
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        int monthIndex = Integer.parseInt(month) - 1;
        return months[monthIndex] + " " + year;
    }
}
