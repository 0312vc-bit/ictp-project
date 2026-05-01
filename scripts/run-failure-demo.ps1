Write-Host "Running ICTP suite with one intentional failing test for viva demo..."
mvn -s .mvn/settings.xml "-Dictp.demo.failOneTest=true" test
