pipeline {
    agent any

    tools {
        maven 'maven'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Build and Run with Docker Compose') {
            steps {
                sh 'docker-compose build; docker-compose up -d'
            }
        }
    }
}
