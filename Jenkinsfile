pipeline {
    agent any

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

        stage('SonarQube Analysis') {
          steps {
            withSonarQubeEnv('sonar') {
              sh 'mvn sonar:sonar'
            }
          }
        }

        stage('Quality Gate') {
          steps {
            timeout(time: 1, unit: 'MINUTES') {
              waitForQualityGate abortPipeline: true
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