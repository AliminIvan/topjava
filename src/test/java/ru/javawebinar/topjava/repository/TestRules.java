package ru.javawebinar.topjava.repository;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TestRules {
    private static final Logger log = LoggerFactory.getLogger("logger");
    private static final StringBuilder allTestsTime = new StringBuilder();

    public static final Stopwatch EXECUTION_TIME_FOR_EACH_TEST = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String currentTestTime = String.format("Test method '%s' was executed in %d ms", description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            allTestsTime.append(currentTestTime).append("\n");
            log.info(currentTestTime);
        }
    };

    public static final ExternalResource EXECUTION_TIME_STAT_FOR_ALL_TESTS = new ExternalResource() {
        @Override
        protected void after() {
            log.info("\n\nTest class execution time statistics: \n" + allTestsTime);
        }
    };
}
