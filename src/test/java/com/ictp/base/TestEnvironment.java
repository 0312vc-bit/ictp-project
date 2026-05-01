package com.ictp.base;

import com.ictp.app.DemoAppServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public abstract class TestEnvironment {
    protected static final String BASE_URL = System.getProperty("ictp.baseUrl", "http://127.0.0.1:8085");

    @BeforeSuite(alwaysRun = true)
    public void startDemoApp() {
        DemoAppServer.start(8085);
    }

    @AfterSuite(alwaysRun = true)
    public void stopDemoApp() {
        DemoAppServer.stop();
    }
}
