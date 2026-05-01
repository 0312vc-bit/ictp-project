package com.ictp.base;

import com.ictp.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseUiTest extends TestEnvironment {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setupDriver() {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void teardownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
