pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        PROJECT_DIR = 'C:\\Users\\LENOVO\\Desktop\\Final year project'
    }

    stages {
        stage('Initialize Dashboard') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Reset dashboard to 0 so the user sees it syncing in real-time
                    powershell '''
                        $jsContent = "window.testData = {};`nwindow.zapData = {};"
                        Set-Content -Path dashboard\\report-data.js -Value $jsContent
                    '''
                }
            }
        }

        stage('Checkout') {
            steps {
                echo "In a real Jenkins setup, Git checkout happens here automatically."
            }
        }

        stage('Build Project') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Compiles the Java project
                    bat 'mvn clean compile'
                }
            }
        }

        stage('Automated Tests (UI & API)') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Runs TestNG via Maven Surefire
                    bat "mvn test"
                }
            }
            post {
                always {
                    dir("${env.PROJECT_DIR}") {
                        // Save the generated JSON report
                        archiveArtifacts artifacts: 'reports/test-results.json', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Security Scan (OWASP ZAP)') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Runs the PowerShell ZAP script
                    powershell 'powershell -ExecutionPolicy Bypass -File .\\scripts\\run-zap.ps1'
                }
            }
            post {
                always {
                    dir("${env.PROJECT_DIR}") {
                        // Save the generated ZAP JSON report
                        archiveArtifacts artifacts: 'reports/zap-report.json', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Publish Premium Dashboard') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Convert JSON reports into a JavaScript file to bypass local CORS restrictions
                    powershell '''
                        $testData = if (Test-Path reports\\test-results.json) { Get-Content -Raw -Path reports\\test-results.json } else { "{}" }
                        $zapData = if (Test-Path reports\\zap-report.json) { Get-Content -Raw -Path reports\\zap-report.json } else { "{}" }
                        $jsContent = "window.testData = $testData;`nwindow.zapData = $zapData;"
                        Set-Content -Path dashboard\\report-data.js -Value $jsContent
                    '''
                    archiveArtifacts artifacts: 'dashboard/**/*', allowEmptyArchive: false
                }
            }
        }
    }

    post {
        success {
            echo 'ICTP DevSecOps pipeline completed successfully! Review dashboard for results.'
        }
        failure {
            echo 'Pipeline failed! Check the console output and reports.'
        }
    }
}
