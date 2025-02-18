package utils;

import configs.dataPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HtmlTestReport {
    private StringBuilder htmlContent;
    private int totalScenarios = 0;
    private int totalTests = 0;
    private int totalPassed = 0;
    private int totalFailed = 0;
    private Map<String, StringBuilder> scenariosMap = new HashMap<>();
    private Map<String, Boolean> scenarioStatus = new HashMap<>();
    private Map<String, String> scenarioBookingRefs = new HashMap<>();
    private Map<String, ScenarioStats> scenarioStats = new HashMap<>();

    private static class ScenarioStats {
        int total = 0;
        int passed = 0;
        int failed = 0;
        String bookingRef = "";
    }

    private final String CSS = """
            body {
                font-family: Arial, sans-serif;
                line-height: 1.6;
                max-width: 1200px;
                margin: 0 auto;
                padding: 20px;
                background-color: #f5f5f5;
            }
            .container {
                background-color: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .header {
                text-align: center;
                margin-bottom: 30px;
            }
            .pass { color: #2e7d32; font-weight: bold; }
            .fail { color: #d32f2f; font-weight: bold; }
            .scenario {
                font-weight: bold;
                cursor: pointer;
                margin: 10px 0;
                padding: 10px;
                background-color: #e3f2fd;
                border-radius: 4px;
                transition: background-color 0.2s;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .scenario:hover { background-color: #bbdefb; }
            .scenario-info {
                display: flex;
                justify-content: space-between;
                align-items: center;
                width: 100%;
                margin-right: 10px;
            }
            .scenario-stats {
                font-size: 0.9em;
                color: #666;
            }
            .scenario-status {
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 0.9em;
                min-width: 60px;
                text-align: center;
            }
            .status-pass {
                background-color: #c8e6c9;
                color: #2e7d32;
            }
            .status-fail {
                background-color: #ffcdd2;
                color: #d32f2f;
            }
            .testcases {
                display: none;
                padding: 20px;
                margin: 10px 0;
                border-left: 3px solid #2196f3;
                background-color: #fff;
            }
            .booking-ref {
                background-color: #f5f5f5;
                padding: 10px;
                margin-bottom: 15px;
                border-radius: 4px;
                border-left: 3px solid #2196f3;
            }
            .failure-message {
                 background-color: #fff3f3;
                 padding: 10px;
                 margin-top: 10px;
                 border-left: 3px solid #d32f2f;
                 font-family: monospace;
             }
            .summary {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 20px;
                margin: 20px 0;
                padding: 20px;
                background-color: #fff;
                border-radius: 4px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            .stat {
                text-align: center;
                padding: 10px;
                border-radius: 4px;
                background-color: #f8f9fa;
            }
            .stat-value {
                font-size: 24px;
                font-weight: bold;
                margin-bottom: 5px;
            }
            .chart-container {
                width: 400px;
                height: 400px;
                margin: 20px auto;
                position: relative;
            }
            hr { border-top: 1px solid #e0e0e0; margin: 10px 0; }
            #searchInput {
                width: 100%;
                padding: 12px;
                margin: 10px 0;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }
            """;

    private final String JAVASCRIPT = """
            function toggleTestCases(id) {
                const element = document.getElementById(id);
                element.style.display = element.style.display === 'none' ? 'block' : 'none';
            }
            
            function drawChart() {
                const ctx = document.getElementById('testChart').getContext('2d');
                new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: ['Passed (%d)', 'Failed (%d)'],
                        datasets: [{
                            data: [%d, %d],
                            backgroundColor: ['#4CAF50', '#FF6347'],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom'
                            }
                        }
                    }
                });
            }
            
            function searchScenarios() {
                const input = document.getElementById('searchInput').value.toLowerCase();
                const scenarios = document.getElementsByClassName('scenario');
                Array.from(scenarios).forEach(scenario => {
                    scenario.style.display = scenario.textContent.toLowerCase().includes(input) ? 'flex' : 'none';
                });
            }
            
            window.onload = drawChart;
            """;

    public HtmlTestReport() {
        createHTMLReport();
    }

    public void createHTMLReport() {
        htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>")
                .append("<html lang='en'><head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Test Execution Report</title>")
                .append("<style>").append(CSS).append("</style>")
                .append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>")
                .append("</head><body>")
                .append("<div class='container'>")
                .append("<div class='header'>")
                .append("<h1>Test Execution Report</h1>")
                .append("<p>Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</p>")
                .append("</div>")
                .append("<div class='chart-container'><canvas id='testChart'></canvas></div>")
                .append("<div class='summary'>")
                .append("<div class='stat'><div class='stat-value'>").append(totalScenarios).append("</div><div>Total Scenarios</div></div>")
                .append("<div class='stat'><div class='stat-value'>").append(totalTests).append("</div><div>Total Tests</div></div>")
                .append("<div class='stat'><div class='stat-value pass'>").append(totalPassed).append("</div><div>Passed</div></div>")
                .append("<div class='stat'><div class='stat-value fail'>").append(totalFailed).append("</div><div>Failed</div></div>")
                .append("</div>")
                .append("<input type='text' id='searchInput' onkeyup='searchScenarios()' placeholder='Search scenarios...'>");


    }

    public synchronized void writeTestReport(String testScenarioID, String testCaseID,
                                             String testCaseSummary, String testStatus,
                                             String bookingRefOrCID, String testCaseBookingRef,
                                             String failureMessage) {
        // Initialize scenario data if new
        if (!scenariosMap.containsKey(testScenarioID)) {
            scenariosMap.put(testScenarioID, new StringBuilder());
            scenarioStatus.put(testScenarioID, true);
            scenarioBookingRefs.put(testScenarioID, bookingRefOrCID);
            scenarioStats.put(testScenarioID, new ScenarioStats());
            totalScenarios++;
        }

        ScenarioStats stats = scenarioStats.get(testScenarioID);
        stats.total++;
        totalTests++;

        if (testStatus.equalsIgnoreCase("pass")) {
            stats.passed++;
            totalPassed++;
        } else {
            stats.failed++;
            totalFailed++;
            scenarioStatus.put(testScenarioID, false);
        }
        stats.bookingRef = bookingRefOrCID;

        StringBuilder scenarioContent = scenariosMap.get(testScenarioID);
        scenarioContent.append("<div class='test-case'>")
                .append("<p><strong>Test Case ID:</strong> ").append(testCaseID)
                .append("<br><strong>Summary:</strong> ").append(testCaseSummary)
                .append("<br><strong>Booking Reference:</strong> ").append(testCaseBookingRef)
                .append("<br><strong>Status:</strong> <span class='").append(testStatus.toLowerCase()).append("'>")
                .append(testStatus).append("</span>");

        if (!testStatus.equalsIgnoreCase("pass") && failureMessage != null && !failureMessage.isEmpty()) {
            scenarioContent.append("<div class='failure-message'>")
                    .append("<strong>Failure Message:</strong><br>")
                    .append(failureMessage.replace("\n", "<br>"))
                    .append("</div>");
        }

        scenarioContent.append("</p></div><hr>");
    }

    public synchronized String saveHTMLReport() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = formatter.format(new Date());
        String fileName = dataPaths.dataBasePath + "\\TestResult\\TestReport_" + timestamp + ".html";

        // Update summary statistics
        htmlContent.append("<script>")
                .append("document.querySelector('.summary .stat-value:nth-child(1)').textContent = '").append(totalScenarios).append("';")
                .append("document.querySelector('.summary .stat-value:nth-child(2)').textContent = '").append(totalTests).append("';")
                .append("document.querySelector('.summary .stat-value.pass').textContent = '").append(totalPassed).append("';")
                .append("document.querySelector('.summary .stat-value.fail').textContent = '").append(totalFailed).append("';")
                .append("</script>");

        // Add scenarios with stats
        for (Map.Entry<String, StringBuilder> entry : scenariosMap.entrySet()) {
            String scenarioID = entry.getKey();
            StringBuilder scenarioContent = entry.getValue();
            boolean isPassing = scenarioStatus.get(scenarioID);
            ScenarioStats stats = scenarioStats.get(scenarioID);

            htmlContent.append("<div class='scenario'>")
                    .append("<div class='scenario-info'>")
                    .append("<span onclick=\"toggleTestCases('").append(scenarioID).append("')\">▶ ").append(scenarioID).append("</span>")
                    .append("<span class='scenario-stats'>")
                    .append("Total: ").append(stats.total)
                    .append(" | Passed: ").append(stats.passed)
                    .append(" | Failed: ").append(stats.failed)
                    .append("</span>")
                    .append("</div>")
                    .append("<span class='scenario-status status-").append(isPassing ? "pass" : "fail").append("'>")
                    .append(isPassing ? "PASS" : "FAIL").append("</span>")
                    .append("</div>")
                    .append("<div id='").append(scenarioID).append("' class='testcases'>")
                    .append("<div class='booking-ref'><strong>Booking Ref/ID:</strong> ").append(stats.bookingRef).append("</div>")
                    .append(scenarioContent.toString())
                    .append("</div>");
        }

        // Add JavaScript and close HTML
        htmlContent.append("<script>")
                .append(String.format(JAVASCRIPT, totalPassed, totalFailed, totalPassed, totalFailed))
                .append("</script>")
                .append("</div></body></html>");

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(htmlContent.toString());
        }

        return fileName;
    }
}