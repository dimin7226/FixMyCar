pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    ./mvnw clean verify
                '''
            }
        }

        stage('Build and Run with Docker Compose') {
            steps {
                sh '''
                    docker compose up --build -d
                '''
            }
        }
    }

    post {
        always {
            echo "Build completed"
            sh 'docker compose ps || true'
        }
    }
}