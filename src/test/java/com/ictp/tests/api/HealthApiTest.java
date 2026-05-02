package com.ictp.tests.api;

import com.ictp.tests.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class HealthApiTest extends BaseTest {

    @Test(description = "Verify Health API returns 200 OK and correct JSON status")
    public void testHealthCheckStatus() {
        RestAssured.baseURI = BASE_URL;

        Response response = given()
            .when()
            .get("/api/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("OK"))
            .body("version", equalTo("1.0.0"))
            .extract().response();
            
        System.out.println("Health API Response: " + response.asString());
    }
}
