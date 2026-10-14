package com.assignment.common.listeners;

import com.assignment.common.reporting.ExtentManager;
import com.assignment.common.reporting.ScreenshotCapable;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ISuite suite) {
        ExtentManager.getInstance(suite.getName().replaceAll("\\s+", "_"));
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String method = result.getMethod().getMethodName();
        String desc = result.getMethod().getDescription();
        // Business-readable title in the report; method name kept as a sub-line for engineers
        String title = (desc == null || desc.isBlank()) ? method : desc;
        if (result.getParameters().length > 0) {
            title += " - " + Arrays.toString(result.getParameters());
        }
        ExtentTest test = ExtentManager.getInstance("Automation_Report")
                .createTest(title, result.getTestClass().getRealClass().getSimpleName() + "." + method)
                .assignCategory(result.getMethod().getGroups());
        ExtentManager.setTest(test);
        LOG.info("===== START: {} =====", title);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().pass("Test passed");
        LOG.info("===== PASS: {} =====", result.getMethod().getMethodName());
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("===== FAIL: {} =====", result.getMethod().getMethodName(), result.getThrowable());
        ExtentTest test = ExtentManager.getTest();
        test.fail(result.getThrowable());
        if (result.getInstance() instanceof ScreenshotCapable sc) {
            String base64 = sc.captureScreenshotBase64();
            if (base64 != null) {
                test.fail("Failure screenshot",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("===== SKIP: {} =====", result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.skip(result.getThrowable() != null ? result.getThrowable().getMessage() : "Skipped");
        }
        ExtentManager.removeTest();
    }
}
