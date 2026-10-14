package com.assignment.common.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Thread-safe ExtentReports holder (supports parallel execution). */
public final class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();
    public static final String REPORT_DIR = "build/reports/extent/";

    private ExtentManager() { }

    public static synchronized ExtentReports getInstance(String reportName) {
        if (extent == null) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_DIR + reportName + "_" + ts + ".html");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle(reportName);
            spark.config().setReportName(reportName);
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", System.getProperty("env", "qa"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }

    public static void setTest(ExtentTest test) { CURRENT.set(test); }

    public static ExtentTest getTest() { return CURRENT.get(); }

    public static void removeTest() { CURRENT.remove(); }

    /** Convenience step logger usable from page objects / API clients. */
    public static void info(String message) {
        ExtentTest test = CURRENT.get();
        if (test != null) test.info(message);
    }

    public static synchronized void flush() {
        if (extent != null) extent.flush();
    }
}
