pipeline{
    agent any 
    // environment {
    //     REPONAME = 'pushpakkautkar'
    //     IMAGE_NAME = 'flight-reservation-cdec-b50'
    // }

    stages{
        stage('checkout'){
            steps{
                 git branch: 'main', url: 'https://github.com/pushpakkautkar/flight-reservation-app'
            }

        }
        stage('build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    mvn clean package 
                '''
            }
        }
        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv(credentialsId: 'sonar-cred', installationName: 'sonar') {
                sh '''
                    cd FlightReservationApplication
                    mvn sonar:sonar -Dsonar.projectKey=flight-reservation
                '''
                }
            }
        }
        stage('Dockerbuild'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build -t pushpakkautkar/flight-reservation-app:latest .
                    docker push pushpakkautkar/flight-reservation-app:latest
                '''
            }
        }
        stage('Deploy to EKS'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}