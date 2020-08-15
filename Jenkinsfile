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
          SERVICE_NAME="book-inventory-service"
          IMAGE_VERSION="v_"${BUILD_NUMBER}
          TASK_FAMILY="book-inventory"

          # Create a new task definition for this build
          sed -e "s;%BUILD_NUMBER%;${BUILD_NUMBER};g" task-def-book-inventory.json > task-def-book-inventory-v_${BUILD_NUMBER}.json
          aws ecs register-task-definition --family book-inventory --cli-input-json task-def-book-inventory-v_${BUILD_NUMBER}.json

          # Update the service with the new task definition and desired count
          TASK_REVISION=`aws ecs describe-task-definition --task-definition book-inventory | egrep "revision" | tr "/" " " | awk '{print $2}' | sed 's/"$//'`
          echo $TASK_REVISION
          DESIRED_COUNT=`aws ecs describe-services --services ${SERVICE_NAME} | egrep "desiredCount" | head -1 | tr "/" " " | awk '{print $2}' | sed 's/,$//'`
          echo $DESIRED_COUNT
          if [ ${DESIRED_COUNT} = "0" ]; then
              DESIRED_COUNT="1"
          fi

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