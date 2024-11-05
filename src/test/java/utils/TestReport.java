package utils;

import configs.dataPaths;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import method.Method;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestReport {

    static Method method = new Method();
    static ExcelTestReport excelTestReport = new ExcelTestReport();
    static HtmlTestReport htmlTestReport = new HtmlTestReport();

    public void updateTestResult(List<String[]> testResultInfo, WebDriver driver, String testScenarioID, String testCaseID, String testCaseSummary, String correlationID, boolean testValidationBoolValue) {
        String testRunStatus;
        String screenShotPath = takeScreenshotIfFailed(driver, testScenarioID, testCaseID, testValidationBoolValue);

        testRunStatus = testValidationBoolValue ? "pass" : "fail";

        String[] testResultsArray = {testScenarioID, testCaseID, testCaseSummary, testRunStatus, correlationID, screenShotPath};
        testResultInfo.add(testResultsArray);

    }

    // Method to update the report with a batch of test cases
    public void updateToReport(List<String[]> testReportList) {
        // Create a local copy of the test report list
        List<String[]> localCopy = new ArrayList<>(testReportList);

        for (String[] testDetails : localCopy) {

            htmlTestReport.writeTestReport(testDetails[0], testDetails[1], testDetails[2], testDetails[3], testDetails[4]);
            excelTestReport.writeTestReport(testDetails[0], testDetails[1], testDetails[2], testDetails[3], testDetails[4]);

            System.out.println("Test report data" +(testDetails[0]+"  "+ testDetails[1]+"  "+  testDetails[2]+"  "+  testDetails[3]+"  "+  testDetails[4]));
        }
        testReportList.clear();
    }

    public void saveReportsAfterTest(List<String> attachmentsPath) throws IOException, InterruptedException {

        String testExcelReportPath = "";
        String testHtmlReportPath = "";

        // Save the Excel report after the test run
        testExcelReportPath = excelTestReport.saveExcelReport();
        testHtmlReportPath = htmlTestReport.saveHTMLReport();

        Thread.sleep(1000);

        attachmentsPath.add(testExcelReportPath);
        attachmentsPath.add(testHtmlReportPath);

        // Remove null values using an iterator
        Iterator<String> iterator = attachmentsPath.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove(); // Remove the null value
            }
        }

    }

    public String takeScreenshotIfFailed(WebDriver driver, String testScenarioID, String testCaseID, boolean testValidationBoolValue) {
        if (!testValidationBoolValue) {
            return takeScreenshot(driver, testScenarioID, testCaseID);
        }
        return null; // No screenshot needed
    }

    public String takeScreenshot(WebDriver driver, String testScenarioID, String testCaseID) {
        String screenShotPath = "";

        // Generate a safe timestamp
        String timeStamp = method.getTimeStamp().replaceAll("[^a-zA-Z0-9_-]", "_");

        // Replace any invalid characters in testScenarioID and testCaseID
        String sanitizedScenarioID = testScenarioID.replaceAll("[^a-zA-Z0-9_-]", "_");
        String sanitizedCaseID = testCaseID.replaceAll("[^a-zA-Z0-9_-]", "_");

        String fileName = sanitizedScenarioID + "_" + sanitizedCaseID + "_TimeStamp_" + timeStamp + ".png";

        // Log the full path for debugging
        System.out.println("Screenshot directory: " + dataPaths.screenshotFolder);
        System.out.println("Generated file name: " + fileName);

        // Take screenshot
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // Set the destination file
        File destinationFile = new File(dataPaths.screenshotFolder + File.separator + fileName);

        try {
            // Copy the screenshot to the destination file
            FileUtils.copyFile(screenshotFile, destinationFile);
            screenShotPath = destinationFile.getAbsolutePath();
            System.out.println("Screenshot saved: " + screenShotPath); // Log successful save
        } catch (IOException e) {
            e.printStackTrace(); // Log stack trace for debugging
            System.out.println("Failed to save screenshot: " + e.getMessage());
        }

        return screenShotPath;
    }
}
