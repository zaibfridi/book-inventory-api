pipeline {
  environment {
    registryName = "ovoh1"
    registry = "ovoh1/book-inventory-api"
    registryCredential = 'dockerhub'
    dockerImage = ''
  }
  agent any
  stages {
    stage('Cloning Git') {
      steps {
        git 'https://github.com/lovely-007/book-inventory-api.git'
      }
    }
    stage('Building jar') {
      steps {
       bat 'mvn clean install'
      }
    }
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('Deploying image to Docker Hub') {
      steps{
        script {
          docker.withRegistry( '', registryCredential ) {
            dockerImage.push()
          }
        }
      }
    }
    stage('Deploy Container on Aws'){
    environment{
      dockerRun = 'docker run -p 5000:5000 -d --name book-inventory-api dockerImage'
     }
    steps{
      sshagent(['aws-server']) {
        sh "ssh -o StrictHostKeyChecking=no ec2-user@10.0.1.137 ${dockerRun}"
         }
       }
     }
  }
}