Write-Host "Running ICTP Maven test suite..."
mvn -s .mvn/settings.xml clean test

if ($LASTEXITCODE -eq 0) {
    Write-Host "Test suite completed. Dashboard data refreshed."
} else {
    Write-Host "Test suite failed. Review Maven output above."
}
