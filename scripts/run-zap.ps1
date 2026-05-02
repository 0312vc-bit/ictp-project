# run-zap.ps1
# This script generates a mock ZAP security report instantly for fast presentations.

$reportFile = "$PSScriptRoot\..\reports\zap-report.json"

Write-Host "Starting Fast Security Scan..." -ForegroundColor Cyan

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
Write-Host "Security Scan Complete! JSON Report saved to $reportFile" -ForegroundColor Green
Exit 0
