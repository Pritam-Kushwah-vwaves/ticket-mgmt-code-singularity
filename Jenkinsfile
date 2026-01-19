pipeline {
  agent any

  environment {
    IMAGE_NAME = "pritam0303/ticket-backend"
    IMAGE_TAG = "latest"
  }

  tools {
    maven 'M3'
  }

  stages {

    stage('Checkout Code') {
      steps {
        git branch: 'main', url: 'https://github.com/Pritam-Kushwah-vwaves/Ticket-management.git'
      }
    }

    stage('Build App') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'dockerhub-creds',
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          sh '''
            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
            docker push $IMAGE_NAME:$IMAGE_TAG
          '''
        }
      }
    }

    // stage('Deploy to Kubernetes (dev)') {
    //   steps {
    //     withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
    //       sh '''
    //         helm upgrade --install ticket-backend ./ticket-backend \
    //         -n dev \
    //         --set image.repository=$IMAGE_NAME \
    //         --set image.tag=$IMAGE_TAG
    //       '''
    //     }
    //   }
    // }
  }
}
