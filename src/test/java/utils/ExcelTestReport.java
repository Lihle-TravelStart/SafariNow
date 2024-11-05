package utils;

import configs.dataPaths;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelTestReport {

    private static Workbook workbook;
    protected static Sheet sheet;
    private static int rowNum = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public void createExcelReport() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Test Results");

        // Creating header row
        Row headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "Test Scenario ID");
        createCell(headerRow, 1, "Test Case ID");
        createCell(headerRow, 2, "Test Case Summary");
        createCell(headerRow, 3, "Test Status");
        createCell(headerRow, 4, "BookingRef or CorrelationID");

        // Formatting: auto-size columns
        autoSizeColumns();
    }

    private void createCell(Row row, int columnIndex, String value) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
    }

    private void autoSizeColumns() {
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Method to write a new row with the test case details
    public void writeTestReport(String testScenarioID, String testCaseID, String testCaseSummary, String testStatus, String bookingRefOrCID) {
        Row row = sheet.createRow(++rowNum);
        createCell(row, 0, testScenarioID);
        createCell(row, 1, testCaseID);
        createCell(row, 2, testCaseSummary);
        createCell(row, 3, testStatus);
        createCell(row, 4, bookingRefOrCID);

        // Update passed/failed counts
        if ("pass".equalsIgnoreCase(testStatus)) {
            passedTests++;
        } else {
            failedTests++;
        }
    }

    // Method to save the Excel file with a unique name (based on timestamp)
    public String saveExcelReport() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String timestamp = formatter.format(date);

        // Generate a new filename using the timestamp
        String fileName = dataPaths.dataBasePath + "\\TestResult\\TestReport_" + timestamp + ".xlsx";

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            System.out.println("Failed to save the Excel report: " + e.getMessage());
            throw e; // Re-throw the exception for further handling
        } finally {
            workbook.close();
        }

        System.out.println("Test report generated: " + fileName);
        return fileName;
    }

    // Method to add summary at the end of the report
    public void addSummary() {
        Row summaryRow = sheet.createRow(++rowNum);
        createCell(summaryRow, 0, "Total Tests");
        createCell(summaryRow, 1, String.valueOf(rowNum));

        Row passedRow = sheet.createRow(++rowNum);
        createCell(passedRow, 0, "Passed Tests");
        createCell(passedRow, 1, String.valueOf(passedTests));

        Row failedRow = sheet.createRow(++rowNum);
        createCell(failedRow, 0, "Failed Tests");
        createCell(failedRow, 1, String.valueOf(failedTests));

        autoSizeColumns(); // Resize columns to fit summary
    }
}
