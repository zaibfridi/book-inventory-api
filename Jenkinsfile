node('master'){
    env.service_name = "book-inventory" 
    env.docker_registry = "095861561296.dkr.ecr.us-east-1.amazonaws.com/book-inventory" 
    env.imagename = "book-inventory-latest"
    env.cluster_name="book-inventory-cluster"  
    env.region= "us-east-1"
    env.task_family="book-inventory-app"
 
    

stage('CleanWs'){
        cleanWs()
    }

    dir('book-inventory') {
                git 'https://github.com/lovely-007/book-api.git'
    }  
   
stage('Build Common'){
         bash ./gradlew build
    }
stage('Login to ap-south-1 ECR'){
      sh '$(aws ecr get-login --no-include-email --region us-east-1)'
    }

stage('BuildDockerImage'){
      dir("book-inventory"){
        sh """
        docker build . --tag \${docker_registry}/\${imagename}:\${service_name}\${BUILD_NUMBER}
        docker push \${docker_registry}/\${imagename}:\${service_name}\${BUILD_NUMBER}
        """
      }
    }
stage('ECS Deployment'){
         sh """
             NEW_DOCKER_IMAGE=\${docker_registry}/\${imagename}:\${service_name}\${BUILD_NUMBER}
             TASK_DEF_OLD=\$(aws ecs describe-task-definition --task-definition  \$task_family --region \$region)
             TASK_DEF_NEW=\$(echo \$TASK_DEF_OLD | jq --arg NDI \$NEW_DOCKER_IMAGE '.taskDefinition.containerDefinitions[0].image=\$NDI')
             TASK_FINAL=\$(echo \$TASK_DEF_NEW | jq '.taskDefinition|{memory: .memory, cpu: .cpu, placementConstraints: .placementConstraints,family: .family, volumes: .volumes, containerDefinitions: .containerDefinitions}')
             FINAL_TASK_FOR_ROLLBACK=\$(echo \$TASK_DEF_OLD | jq '.taskDefinition|{memory: .memory, cpu: .cpu, placementConstraints: .placementConstraints,family: .family, volumes: .volumes, containerDefinitions: .containerDefinitions}')
             echo -n \$FINAL_TASK_FOR_ROLLBACK > FINAL_TASK_FOR_ROLLBACK
             aws ecs register-task-definition --family \$task_family --region \$region --cli-input-json "\$(echo \$TASK_FINAL)"
             aws ecs update-service --service \$service_name --region \$region --task-definition \$task_family --cluster \$cluster_name

         """
     }  
     stage('Verifying Deployment'){
         println "Waiting service to reached steady-state"
         try{
             sh "aws ecs wait services-stable --region \$region --cluster \$cluster_name --services \$service_name"
             rollback=false
         }
         catch (Exception e){
             //deployment failed, do rollback
             rollback=true
         }
         finally{
             //leaving it empty for future use
     
         }
     }

     
     if (rollback){
           stage ('rollback deployment'){
               println "Ready for deployment roll back"
               sh """
               aws ecs register-task-definition --region \$region --family \$task_family --cli-input-json "\$(cat FINAL_TASK_FOR_ROLLBACK)"
               aws ecs update-service --region \$region --service \$service_name --task-definition \$task_family --cluster \$cluster_name
               """
           }
       }
     
     if (rollback){
         error 'Deployment failed: Had to rollback'
     }

}