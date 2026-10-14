package com.assignment.common.reporting;

/** Implemented by UI base tests so the shared listener can attach failure screenshots. */
public interface ScreenshotCapable {
    /** @return Base64 encoded PNG, or null if not available. */
    String captureScreenshotBase64();
}
