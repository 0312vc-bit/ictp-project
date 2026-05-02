window.testData = {
  "total" : 3,
  "tests" : [ {
    "description" : "Verify failed login with invalid credentials",
    "durationMs" : "1183",
    "testName" : "testInvalidLogin",
    "status" : "PASSED"
  }, {
    "description" : "Verify successful login with valid credentials",
    "durationMs" : "65",
    "testName" : "testValidLogin",
    "status" : "PASSED"
  }, {
    "description" : "Verify Health API returns 200 OK and correct JSON status",
    "durationMs" : "6251",
    "testName" : "testHealthCheckStatus",
    "status" : "PASSED"
  } ],
  "passed" : 3,
  "failed" : 0,
  "suiteName" : "ICTP Continuous Test Suite",
  "skipped" : 0
};
window.zapData = {
    "@version": "2.14.0",
    "@generated": "Sat, 02 May 2026 16:43:37",
    "site": [
        {
            "@name": "http://localhost:8085",
            "@host": "localhost",
            "@port": "8085",
            "alerts": [
                {
                    "pluginId": "10021",
                    "alert": "X-Content-Type-Options Header Missing",
                    "riskdesc": "Low (Medium)",
                    "desc": "The Anti-MIME-Sniffing header X-Content-Type-Options was not set to 'nosniff'."
                },
                {
                    "pluginId": "10015",
                    "alert": "Incomplete or No Cache-control Header Set",
                    "riskdesc": "Low (Medium)",
                    "desc": "The cache-control header has not been set properly or is missing."
                }
            ]
        }
    ]
}
;
