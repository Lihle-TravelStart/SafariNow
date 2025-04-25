package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import configs.dataPaths;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
    private static ExtentReports extent;
    private static ExtentTest test;

    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter spark = new ExtentSparkReporter(dataPaths.dataBasePath+"\\TestResult\\report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        test.pass(testSummary);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        test.fail(result.getThrowable()).getModel().setDescription(testSummary);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        test.skip(result.getThrowable()).getModel().setDescription(testSummary);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}