pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK20'
    }

    triggers {
        pollSCM('H/2 * * * *')        // on push: poll every ~2 min
        cron('H 2 * * *')             // nightly: ~2 AM full regression
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['global', 'dev', 'test'],
                description: 'Which properties file to use (-Denv)')
        string(name: 'TAGS', defaultValue: '',
                description: 'Override tags. Leave blank to auto-select by trigger.')
    }

    stages {
        stage('Determine Tags') {
            steps {
                script {
                    // If a human set TAGS via "Build with Parameters", respect it.
                    if (params.TAGS?.trim()) {
                        env.RUN_TAGS = params.TAGS
                    } else {
                        // Otherwise pick based on what triggered the build.
                        def causes = currentBuild.getBuildCauses()
                        def isTimer = causes.any { it._class?.contains('TimerTrigger') }
                        env.RUN_TAGS = isTimer ? '@endtoend' : '@smoke'
                    }
                    echo "Running with tags: ${env.RUN_TAGS}"
                }
            }
        }
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build & Test in Docker') {
            steps {
                script {
                    // Build the image, tagged with the build number for traceability
                    sh "docker build -t restassured-bdd:${BUILD_NUMBER} ."

                    // Run the suite inside the container.
                    // Mount target/ out so reports land on the Jenkins workspace.
                    sh """
                docker run --rm \
                    -v \$(pwd)/target:/app/target \
                    restassured-bdd:${BUILD_NUMBER} \
                    clean verify -Denv=${params.ENVIRONMENT} -Dcucumber.filter.tags="${env.RUN_TAGS}"
            """
                }
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