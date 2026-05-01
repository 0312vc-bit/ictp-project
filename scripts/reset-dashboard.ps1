param(
    [string]$BuildNumber = "ICTP-DEMO-RESET"
)

$payload = @{
    generatedAt = (Get-Date -Format "yyyy-MM-dd HH:mm:ss")
    pipelineStatus = "READY"
    buildNumber = $BuildNumber
    browserSuite = "HtmlUnit Selenium"
    apiCoverage = "GET and POST validation"
    securityTool = "OWASP ZAP Baseline"
    totalTests = 0
    passedTests = 0
    failedTests = 0
    securityIssues = 0
    stages = @(
        @{ name = "Build"; status = "READY" }
        @{ name = "UI Tests"; status = "READY" }
        @{ name = "API Tests"; status = "READY" }
        @{ name = "Security Scan"; status = "READY" }
        @{ name = "Dashboard Publish"; status = "READY" }
    )
    alerts = @(
        @{ level = "WARN"; message = "Dashboard reset for live presentation. Trigger Jenkins to generate fresh results." }
    )
}

$json = $payload | ConvertTo-Json -Depth 5

$reportPath = Join-Path $PSScriptRoot "..\\reports\\latest-run.json"
$dashboardPath = Join-Path $PSScriptRoot "..\\dashboard\\data\\latest-run.json"

$json | Set-Content -Path $reportPath
$json | Set-Content -Path $dashboardPath

Write-Host "Dashboard reset to zero state."
Write-Host "Reports: $reportPath"
Write-Host "Dashboard data: $dashboardPath"
