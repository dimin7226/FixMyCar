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

        stage('Setup SSL Certificates') {
            steps {
                withCredentials([
                    file(credentialsId: 'fixmycar-ssl-cert', variable: 'CERT_FILE'),
                    file(credentialsId: 'fixmycar-ssl-key', variable: 'KEY_FILE'),
                    file(credentialsId: 'fixmycar-ssl-ca', variable: 'CA_FILE')
                ]) {
                    sh '''
                        mkdir -p ./ssl/fixmycar.2bd.net
                        cp $CERT_FILE ./ssl/fixmycar.2bd.net/fullchain.cer
                        cp $KEY_FILE ./ssl/fixmycar.2bd.net/fixmycar.2bd.net.key
                        cp $CA_FILE ./ssl/fixmycar.2bd.net/ca.cer
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
                    docker-compose up --build -d
                '''
            }
        }
    }

    post {
        always {
            echo "Build completed"
            sh 'docker-compose ps || true'
        }
    }
}