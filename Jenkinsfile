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
        git 'https://github.com/Egwonor/book-inventory-api.git'
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
  }
}