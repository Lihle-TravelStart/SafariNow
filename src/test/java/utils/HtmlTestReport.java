package utils;

import configs.dataPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HtmlTestReport {

    private static StringBuilder htmlContent;
    private static int testCount = 0;

    // Method to create the HTML report structure
    public void createHTMLReport() {
        htmlContent = new StringBuilder();

        // Starting HTML structure with CSS and JavaScript for collapsible rows
        htmlContent.append("<html><head><title>Test Report</title>")
                .append("<style>")
                .append("table {width: 100%; border-collapse: collapse;}")
                .append("table, th, td {border: 1px solid black; padding: 8px; text-align: left;}")
                .append("th {background-color: #f2f2f2;}")
                .append("tr:nth-child(even) {background-color: #f9f9f9;}")
                .append("tr:nth-child(odd) {background-color: #fff;}")
                .append(".pass {color: green;}")
                .append(".fail {color: red;}")
                .append(".scenario-row {cursor: pointer; background-color: #e9f7fd;}")
                .append(".details-row {display: none;}")
                .append("</style>")
                .append("<script>")
                .append("function toggleDetails(scenarioId) {")
                .append("  var rows = document.getElementsByClassName(scenarioId);")
                .append("  for (var i = 0; i < rows.length; i++) {")
                .append("    rows[i].style.display = rows[i].style.display === 'none' ? 'table-row' : 'none';")
                .append("  }")
                .append("}")
                .append("</script>")
                .append("</head><body>");

        // Adding a header
        htmlContent.append("<h1>Test Report</h1>");
        htmlContent.append("<table><tr>")
                .append("<th>Test Scenario ID</th>")
                .append("<th>Test Case ID</th>")
                .append("<th>Test Case Summary</th>")
                .append("<th>Test Status</th>")
                .append("<th>Booking Reference or Correlation ID</th>")
                .append("</tr>");
    }

    // Method to write a new test case result into the HTML report
    public void writeTestReport(String testScenarioID, String testCaseID, String testCaseSummary, String testStatus, String bookingRefOrCID) {
        // Scenario row (clickable to toggle details)
        htmlContent.append("<tr class='scenario-row' onclick=\"toggleDetails('scenario-").append(testScenarioID).append("')\">")
                .append("<td>").append(testScenarioID).append("</td>")
                .append("<td colspan='4'>Click to expand</td>")
                .append("</tr>");

        // Detail row (initially hidden)
        htmlContent.append("<tr class='details-row scenario-").append(testScenarioID).append("'>")
                .append("<td></td>")  // Empty cell under scenario column
                .append("<td>").append(testCaseID).append("</td>")
                .append("<td>").append(testCaseSummary).append("</td>")
                .append("<td class='").append(testStatus.toLowerCase()).append("'>").append(testStatus).append("</td>")
                .append("<td>").append(bookingRefOrCID).append("</td>")
                .append("</tr>");

        testCount++;
    }

    // Method to save the HTML file with a unique name (based on timestamp)
    public String saveHTMLReport() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String timestamp = formatter.format(date);

        // Generate a new filename using the timestamp
        String fileName = dataPaths.dataBasePath + "\\TestResult\\TestReport_" + timestamp + ".html";

        // Closing the HTML structure
        htmlContent.append("</table><br><br>")
                .append("<p>Total Test Cases: ").append(testCount).append("</p>")
                .append("</body></html>");

        // Write the content to the HTML file
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(htmlContent.toString());
        }

        System.out.println("Test report generated: " + fileName);
        return fileName;
    }
}
