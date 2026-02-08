pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                withCredentials([file(credentialsId: 'fixmycar-dev-env', variable: 'ENV_FILE')]) {
                    sh '''
                        rm -f .env 2>/dev/null || true
                        cp $ENV_FILE .env
                    '''
                }
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
                    docker-compose down || true
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