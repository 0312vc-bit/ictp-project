package com.ictp.tests;

import com.ictp.app.DummyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;

public class BaseTest {

    protected static WebDriver driver;
    protected static final String BASE_URL = "http://localhost:" + DummyServer.PORT;

    @BeforeSuite
    public void globalSetup() throws IOException {
        // Start the dummy server before tests run
        DummyServer.start();
        
        // Initialize HtmlUnitDriver (Headless browser for fast CI/CD execution)
        driver = new HtmlUnitDriver(true); // true enables JavaScript support
    }

    @AfterSuite
    public void globalTearDown() {
        if (driver != null) {
            driver.quit();
        }
        // Stop the server after tests complete
        DummyServer.stop();
    }
}
