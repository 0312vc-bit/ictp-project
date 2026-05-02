# Intelligent Continuous Testing Platform (ICTP)

ICTP is a self-contained MCA final year project that demonstrates how modern software teams automate build validation, UI testing, API testing, security checks, and dashboard reporting after every code push.

## What is included

- Java 17 + Maven project
- Selenium UI automation using TestNG
- REST Assured API tests
- Jenkins pipeline for CI/CD flow
- OWASP ZAP integration script for security scanning
- HTML/CSS/JavaScript dashboard that reads generated JSON reports
- A lightweight demo application and API built with Java `HttpServer`

## Quick structure

```text
ICTP
├── dashboard
├── reports
├── scripts
├── src
│   ├── main/java/com/ictp/app
│   └── test/java/com/ictp
├── Jenkinsfile
├── pom.xml
└── testng.xml
```

## Local run

1. Run `mvn clean test`
2. Open `dashboard/index.html`
3. Review generated artifacts in `reports/`
adding a new feature

## Notes

- UI tests use HtmlUnitDriver, so they run without a separate browser driver.
- The ZAP stage is production-style integration. If ZAP is not installed, the security stage creates a skipped report instead of failing the full demo.
