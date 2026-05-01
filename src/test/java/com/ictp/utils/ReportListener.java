package com.ictp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportListener implements ITestListener {

    private Map<String, Object> testReport = new HashMap<>();
    private List<Map<String, String>> tests = new ArrayList<>();
    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Starting Test Suite execution...");
        testReport.put("suiteName", context.getSuite().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed++;
        addTestDetails(result, "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed++;
        addTestDetails(result, "FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped++;
        addTestDetails(result, "SKIPPED");
    }

    private void addTestDetails(ITestResult result, String status) {
        Map<String, String> testDetails = new HashMap<>();
        testDetails.put("testName", result.getMethod().getMethodName());
        testDetails.put("description", result.getMethod().getDescription());
        testDetails.put("status", status);
        
        long duration = result.getEndMillis() - result.getStartMillis();
        testDetails.put("durationMs", String.valueOf(duration));
        
        tests.add(testDetails);
    }

    @Override
    public void onFinish(ITestContext context) {
        testReport.put("total", passed + failed + skipped);
        testReport.put("passed", passed);
        testReport.put("failed", failed);
        testReport.put("skipped", skipped);
        testReport.put("tests", tests);

        // Ensure reports directory exists
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Write out JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("reports/test-results.json"), testReport);
            System.out.println("Custom JSON test report generated at: reports/test-results.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
