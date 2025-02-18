package method;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ExcelUtils {

    private static Workbook workbook;
    protected static Sheet sheet;
    private static int rowNum = 0;

    // Method to create a new Excel file with a unique name
    public void createExcelReport() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Test Results");

        // Creating header row
        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("Test Scenario ID");

        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("Itinerary ID");

        Cell cell3 = headerRow.createCell(2);
        cell3.setCellValue("Deeplink");

        Cell cell4 = headerRow.createCell(3);
        cell4.setCellValue("Amount");

        Cell cell5 = headerRow.createCell(4);
        cell5.setCellValue("FlightNumbers");

        Cell cell6 = headerRow.createCell(5);
        cell6.setCellValue("Amount Matched?");

        Cell cell7 = headerRow.createCell(6);
        cell7.setCellValue("Flight Numbers Matched?");

        // Formatting: auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    // Method to write a new row with the test case details
    public void writeToExcelReport(String testScenarioID, String itineraryID, String deeplink, String amount, String flightNumbers, boolean isAMountMatched, boolean isFlightNumbersMatched) {
        Row row = sheet.createRow(++rowNum);

        Cell cell1 = row.createCell(0);
        cell1.setCellValue(testScenarioID);

        Cell cell2 = row.createCell(1);
        cell2.setCellValue(itineraryID);

        Cell cell3 = row.createCell(2);
        cell3.setCellValue(deeplink);

        Cell cell4 = row.createCell(3);
        cell4.setCellValue(amount);

        Cell cell5 = row.createCell(4);
        cell5.setCellValue(flightNumbers);

        Cell cell6 = row.createCell(5);
        cell6.setCellValue(isAMountMatched);

        Cell cell7 = row.createCell(6);
        cell7.setCellValue(isFlightNumbersMatched);
    }

    // Method to save the Excel file with a unique name (based on timestamp)
    public String saveExcelReport() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String timestamp = formatter.format(date);

        // Generate a new filename using the timestamp
        String fileName = "C:\\Users\\Sreen\\IdeaProjects\\travelStart\\TestResult\\TestReport_" + timestamp + ".xlsx";

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }

        workbook.close();
        System.out.println("Test report generated: " + fileName);
        return fileName;
    }

    public int getRowCount(String filePath, String sheetName) {
        int rowCount = 0;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook;
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis); // For XLSX (Excel 2007+) format
            } else if (filePath.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis); // For XLS (Excel 97-2003) format
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }
            Sheet sheet = workbook.getSheet(sheetName);
            rowCount = sheet.getPhysicalNumberOfRows();
            workbook.close();
            fis.close();
        } catch (IOException e) {

        }
        return rowCount;
    }

    public String readDataFromExcel(String filePath, String sheetName, int row, int col) throws IOException {
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row excelRow = sheet.getRow(row);
            Cell cell = excelRow.getCell(col);

            // Use DataFormatter to get formatted string representation of cell value
            DataFormatter dataFormatter = new DataFormatter();
            return dataFormatter.formatCellValue(cell);
        }
    }

    public String[] readExcelColumn(String filePath, String sheett, int col) {
        String[] columnValues = null;
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheet(sheett);

            int rows = sheet.getPhysicalNumberOfRows();
            columnValues = new String[rows];

            for (int i = 0; i < rows; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(col);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                columnValues[i] = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                columnValues[i] = String.valueOf(cell.getNumericCellValue());
                                break;
                            // Handle other cell types as needed
                            default:
                                // Handle other cell types or throw an exception
                                break;
                        }
                    }
                }
            }
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
        return columnValues;
    }

}
