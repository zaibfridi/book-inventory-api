pipeline {
  environment {
    registryName = "zaibfridi"
    registry = "zaibfridi/book-inventory"
    registryCredential = 'shahzaibDockerHub'
    dockerImage = ''
  }
  agent any
  stages {
    stage('Cloning Git') {
      steps {
        git 'https://github.com/zaibfridi/book-inventory-api.git'
      }
    }
    stage('Building jar') {
      steps {
       sh 'mvn clean install'
      }
    }
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build registry + ":v_$BUILD_NUMBER"
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
    stage('Start Task on ECS Cluster') {
      steps{
        sh '''#!/bin/bash -x
          SERVICE_NAME="fargate-service-b"
          IMAGE_VERSION="v_"${BUILD_NUMBER}
          TASK_FAMILY="task-fargate-b"

          # Create a new task definition for this build
          sed -e "s;%BUILD_NUMBER%;${BUILD_NUMBER};g" task-def-fargate.json > task-def-fargate-v_${BUILD_NUMBER}.json
          aws ecs register-task-definition --cli-input-json file://task-def-fargate-v_${BUILD_NUMBER}.json

          # Update the service with the new task definition and desired count
          TASK_REVISION=`aws ecs describe-task-definition --task-definition task-fargate-b | egrep "revision" | tr "/" " " | awk '{print $2}' | sed 's/"$//'`
          echo $TASK_REVISION
          DESIRED_COUNT="1"

          aws ecs update-service --cluster default --service ${SERVICE_NAME} --task-definition ${TASK_FAMILY} --desired-count ${DESIRED_COUNT}'''
      }
    }
    stage('Remove Unused docker image') {
      steps{
        sh "docker rmi $registry:v_$BUILD_NUMBER"
      }
    }
  }
}