package com.ictp.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ISuiteListener;
import org.testng.ITestContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportManager implements ISuiteListener {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onFinish(ISuite suite) {
        int passed = 0;
        int failed = 0;
        List<Map<String, String>> stageSummaries = new ArrayList<>();

        for (ISuiteResult result : suite.getResults().values()) {
            ITestContext context = result.getTestContext();
            passed += context.getPassedTests().size();
            failed += context.getFailedTests().size();
            stageSummaries.add(Map.of(
                    "name", context.getName(),
                    "status", context.getFailedTests().size() == 0 ? "SUCCESS" : "FAILED"
            ));
        }

        int total = passed + failed;
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("generatedAt", LocalDateTime.now().format(FORMATTER));
        dashboard.put("pipelineStatus", failed == 0 ? "SUCCESS" : "FAILED");
        dashboard.put("buildNumber", "ICTP-DEMO-001");
        dashboard.put("browserSuite", "HtmlUnit Selenium");
        dashboard.put("apiCoverage", "GET and POST validation");
        dashboard.put("securityTool", "OWASP ZAP Baseline");
        dashboard.put("totalTests", total);
        dashboard.put("passedTests", passed);
        dashboard.put("failedTests", failed);
        dashboard.put("securityIssues", readSecurityIssues());
        dashboard.put("stages", mergeStages(stageSummaries));
        dashboard.put("alerts", buildAlerts(failed));

        writeJson(Path.of("reports", "latest-run.json"), dashboard);
        writeJson(Path.of("dashboard", "data", "latest-run.json"), dashboard);
    }

    private List<Map<String, String>> mergeStages(List<Map<String, String>> stageSummaries) {
        List<Map<String, String>> stages = new ArrayList<>();
        stages.add(Map.of("name", "Build", "status", "SUCCESS"));
        stages.add(Map.of("name", "UI Tests", "status", statusFor("UI Automation Tests", stageSummaries)));
        stages.add(Map.of("name", "API Tests", "status", statusFor("API Automation Tests", stageSummaries)));
        stages.add(Map.of("name", "Security Scan", "status", readSecurityStatus()));
        stages.add(Map.of("name", "Dashboard Publish", "status", "SUCCESS"));
        return stages;
    }

    private String statusFor(String name, List<Map<String, String>> stageSummaries) {
        return stageSummaries.stream()
                .filter(stage -> stage.get("name").equals(name))
                .map(stage -> stage.get("status"))
                .findFirst()
                .orElse("SKIPPED");
    }

    private List<Map<String, String>> buildAlerts(int failed) {
        List<Map<String, String>> alerts = new ArrayList<>();
        String securityStatus = readSecurityStatus();
        alerts.add(Map.of(
                "level", failed == 0 ? "PASS" : "FAIL",
                "message", failed == 0
                        ? "All automated validation suites completed successfully."
                        : "One or more suites failed. Review the generated reports for details."
        ));
        alerts.add(Map.of(
                "level", Files.exists(Path.of("reports", "zap-report.json")) && "SUCCESS".equalsIgnoreCase(securityStatus) ? "PASS" : "WARN",
                "message", !Files.exists(Path.of("reports", "zap-report.json"))
                        ? "OWASP ZAP report not found. Run scripts/run-zap.ps1 to attach security scan data."
                        : "SUCCESS".equalsIgnoreCase(securityStatus)
                        ? "OWASP ZAP baseline scan completed and is connected to the dashboard."
                        : "OWASP ZAP integration is connected in demo mode. Install ZAP and rerun the scan for live findings."
        ));
        return alerts;
    }

    private int readSecurityIssues() {
        Path path = Path.of("reports", "zap-report.json");
        if (!Files.exists(path)) {
            return 0;
        }

        try {
            Map<?, ?> content = OBJECT_MAPPER.readValue(path.toFile(), Map.class);
            Object issues = content.get("highRiskCount");
            return issues instanceof Number ? ((Number) issues).intValue() : 0;
        } catch (IOException exception) {
            return 0;
        }
    }

    private String readSecurityStatus() {
        Path path = Path.of("reports", "zap-report.json");
        if (!Files.exists(path)) {
            return "SKIPPED";
        }

        try {
            Map<?, ?> content = OBJECT_MAPPER.readValue(path.toFile(), Map.class);
            Object status = content.get("status");
            return status == null ? "SUCCESS" : status.toString();
        } catch (IOException exception) {
            return "SUCCESS";
        }
    }

    private void writeJson(Path path, Map<String, Object> payload) {
        try {
            Files.createDirectories(path.getParent());
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), payload);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write dashboard report to " + path, exception);
        }
    }
}
