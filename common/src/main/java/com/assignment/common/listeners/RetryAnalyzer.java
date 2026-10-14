package com.assignment.common.listeners;

import com.assignment.common.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Retries flaky tests (e.g. public demo endpoints) up to "retry.count" times. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigManager.getInt("retry.count", 1);
        if (attempt < max) {
            attempt++;
            LOG.warn("Retrying {} (attempt {}/{})", result.getName(), attempt, max);
            return true;
        }
        return false;
    }
}
