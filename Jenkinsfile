pipeline {
    agent any

    parameters {
        booleanParam(name: 'DEMO_FAIL', defaultValue: false, description: 'Enable one intentional failing test for viva demonstration')
    }

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        PROJECT_DIR = 'C:\\Users\\LENOVO\\Desktop\\Final year project'
    }

    stages {
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
                    // Runs TestNG via Maven Surefire, passing the DEMO_FAIL parameter
                    bat "mvn test -DdemoFail=${params.DEMO_FAIL}"
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
