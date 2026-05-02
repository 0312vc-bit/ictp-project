package com.ictp.tests.ui;

import com.ictp.pages.LoginPage;
import com.ictp.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginUiTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        driver.get(BASE_URL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        
        loginPage.loginAs("admin", "password123");
        
        String msg = loginPage.getMessage();
        Assert.assertEquals(msg, "Login Successful!", "Success message should be displayed.");
    }

    @Test(description = "Verify failed login with invalid credentials")
    public void testInvalidLogin() {
        // Intentionally fail the test if the DEMO_FAIL parameter is true
        boolean demoFail = Boolean.parseBoolean(System.getProperty("demoFail", "false"));
        if (demoFail) {
            Assert.fail("INTENTIONAL FAILURE: This test was forced to fail for the Viva Demonstration.");
        }

        driver.get(BASE_URL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        
        loginPage.loginAs("wronguser", "wrongpass");
        
        String msg = loginPage.getMessage();
        Assert.assertEquals(msg, "Invalid Credentials", "Error message should be displayed.");
    }
}
