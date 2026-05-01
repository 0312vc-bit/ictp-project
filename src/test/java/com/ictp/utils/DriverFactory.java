package com.ictp.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(true);
        driver.setJavascriptEnabled(true);
        return driver;
    }
}
