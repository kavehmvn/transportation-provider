pipeline {
    agent any
    tools {
        maven 'MAVEN'
    }
    stages {
        stage('Build') {
            steps {
                    checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/kavehmvn/transportation-provider.git']]])
                    sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                    sh 'mvn test'
            }

            post{
                always{
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }
}
