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
        driver.get(BASE_URL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        
        loginPage.loginAs("wronguser", "wrongpass");
        
        String msg = loginPage.getMessage();
        Assert.assertEquals(msg, "Invalid Credentials", "Error message should be displayed.");
    }
}
