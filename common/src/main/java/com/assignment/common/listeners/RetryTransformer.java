package com.assignment.common.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** Applies RetryAnalyzer to every test globally (registered in suite XML). */
public class RetryTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor ctor, Method method) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
