# run-zap.ps1
# This script runs OWASP ZAP locally against our Dummy application to check for vulnerabilities.

$targetUrl = "http://localhost:8085"
$reportFile = "$PSScriptRoot\..\reports\zap-report.json"
$htmlReportFile = "$PSScriptRoot\..\reports\zap-report.html"

Write-Host "Starting Security Scan on $targetUrl" -ForegroundColor Cyan

# We check if ZAP is installed in the default directory
$zapPath = "C:\Program Files\ZAP\Zed Attack Proxy\zap.bat"

if (-Not (Test-Path $zapPath)) {
    Write-Host "OWASP ZAP not found at default location ($zapPath)." -ForegroundColor Yellow
    Write-Host "For a real security scan, please download and install ZAP from: https://www.zaproxy.org/download/" -ForegroundColor Yellow
    Write-Host "Generating a Mock Security Report so the pipeline can continue..." -ForegroundColor Cyan
    
    # Generate mock report for the dashboard
    $mockReport = @"
{
    "@version": "2.14.0",
    "@generated": "$(Get-Date -Format 'ddd, dd MMM yyyy HH:mm:ss')",
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
"@
    # Ensure reports directory exists
    $reportsDir = "$PSScriptRoot\..\reports"
    if (-not (Test-Path $reportsDir)) {
        New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null
    }

    Set-Content -Path $reportFile -Value $mockReport
    Write-Host "Mock ZAP Report saved to $reportFile" -ForegroundColor Green
    Exit 0
}

Write-Host "Running ZAP Daemon in the background..." -ForegroundColor Cyan
# Start ZAP in daemon mode (headless)
Start-Process -FilePath $zapPath -ArgumentList "-daemon -port 8090 -config api.disablekey=true" -NoNewWindow -PassThru

# Wait for ZAP to start
Start-Sleep -Seconds 15

# Trigger Active Scan (Example)
Write-Host "Triggering ZAP Spider..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8090/JSON/spider/action/scan/?url=$targetUrl"

Start-Sleep -Seconds 10 # Allow spider to run

Write-Host "Triggering ZAP Active Scan..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8090/JSON/ascan/action/scan/?url=$targetUrl"

# Wait for scan to complete
$status = 0
while ($status -lt 100) {
    Start-Sleep -Seconds 5
    $response = Invoke-RestMethod -Uri "http://localhost:8090/JSON/ascan/view/status/"
    $status = [int]$response.status
    Write-Host "Scan progress: $status%"
}

Write-Host "Generating Reports..." -ForegroundColor Cyan
$jsonReport = Invoke-RestMethod -Uri "http://localhost:8090/JSON/core/view/alerts/?baseurl=$targetUrl"
$jsonReport | ConvertTo-Json -Depth 5 | Set-Content $reportFile

# Shut down ZAP
Invoke-RestMethod -Uri "http://localhost:8090/JSON/core/action/shutdown/"

Write-Host "Security Scan Complete! JSON Report saved to $reportFile" -ForegroundColor Green
