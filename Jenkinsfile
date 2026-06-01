pipeline {
    agent any

    tools {
        maven 'Maven3'   // must match the name in Jenkins > Manage Jenkins > Tools
        jdk 'JDK20'      // likewise
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    parameters {
        choice(
                name: 'ENVIRONMENT',
                choices: ['global', 'dev', 'test'],
                description: 'Which properties file to use (-Denv)'
        )
        string(
                name: 'TAGS',
                defaultValue: '@endtoend',
                description: 'Cucumber tag filter, e.g. @negative, @addplace, or "@endtoend and not @negative"'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build & Test') {
            steps {
                sh "mvn clean verify -Denv=${params.ENVIRONMENT} -Dcucumber.filter.tags=\"${params.TAGS}\""
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            publishHTML(target: [
                reportDir: 'target/cucumber-html-reports',
                reportFiles: 'overview-features.html',
                reportName: 'Cucumber Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
    }
}