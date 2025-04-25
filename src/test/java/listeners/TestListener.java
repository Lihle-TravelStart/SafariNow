package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        String testSummary = STR."Test: \{result.getMethod().getMethodName()}. ";
        result.setAttribute("testSummary", testSummary);
        System.out.println(STR."Test Started: \{result.getMethod().getMethodName()}");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        testSummary += "The test passed.";
        System.out.println(STR."Test Summary: \{testSummary}");
        result.setAttribute("testSummary", testSummary);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        testSummary += "The test failed.";
        System.out.println("Test Summary: " + testSummary);
        result.setAttribute("testSummary", testSummary);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        testSummary += "The test was skipped.";
        System.out.println(STR."Test Summary: \{testSummary}");
        result.setAttribute("testSummary", testSummary);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testSummary = (String) result.getAttribute("testSummary");
        testSummary += "The test failed but within success percentage.";
        System.out.println(STR."Test Summary: \{testSummary}");
        result.setAttribute("testSummary", testSummary);
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("onStart method started");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("onFinish method started");
    }
}