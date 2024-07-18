def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger']

pipeline {
    agent {
        node {
            label 'java17'
        }
    }

    stages {
        stage('Set Environment') {
            environment {
                //JAVA_HOME = tool 'JDK17'  // Set JAVA_HOME to the JDK configured in Jenkins
                JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
                PATH = "${JAVA_HOME}/bin:${env.PATH}"  // Ensure JAVA_HOME/bin is in the PATH
            }
            steps {
                sh 'echo "JAVA_HOME: $JAVA_HOME"'  // Verify JAVA_HOME in the environment
                sh 'echo "PATH: $PATH"'            // Verify PATH includes JAVA_HOME/bin
            }
        }

        stage('Check Java Version') {
            steps {
                sh 'java -version'
            }
        }

        stage('Cleaning') {
            steps {
                // Clean before build
                cleanWs()
                // We need to explicitly checkout from SCM here
                checkout scm
                echo "Building ${env.JOB_NAME}..."
            }
        }

        stage('Build') {
            steps {
                sh 'ls -al'
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        
        stage('Upload Image') {
           steps {
            
                script{
                    sh "aws ecr get-login-password --region me-central-1 | docker login --username AWS --password-stdin 211125729649.dkr.ecr.me-central-1.amazonaws.com"
                    sh "docker build -t 211125729649.dkr.ecr.me-central-1.amazonaws.com/yy_user_service:${env.BRANCH_NAME}-${env.BUILD_ID} ."
                    sh "docker push 211125729649.dkr.ecr.me-central-1.amazonaws.com/yy_user_service:${env.BRANCH_NAME}-${env.BUILD_ID}"
                      
                   }
             }
         }
         
         stage('Deploy'){

            environment {
                GIT_CREDS = credentials('cbi_bitbucket')
                HELM_REPO_URL = "https://Francispeter_yap@bitbucket.org/yap-technology/yap-ksa-helm.git"
                GIT_REPO_EMAIL = 'francis.peter@yap.com'
                GIT_REPO_USERNAME = "Francispeter_yap"
                GIT_REPO_BRANCH = "${env.BRANCH_NAME}"
                IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
            }
            steps{
                    withCredentials([gitUsernamePassword(credentialsId: 'cbi_bitbucket', gitToolName: 'git-tool')]) {
                
                         sh '''
                           #!/bin/bash
                           if [ -d "yap-ksa-helm" ]
                           then
                             rm -r yap-ksa-helm
                           fi
                         '''
                      sh "git clone ${env.HELM_REPO_URL}"
                      sh "git config --global user.email ${env.GIT_REPO_EMAIL}"
                      sh "git config --global user.name ${env.GIT_REPO_USERNAME}"
                      sh "cd yap-ksa-helm && git checkout yap-young"
                      sh "sudo add-apt-repository ppa:rmescandon/yq"
                      sh "sudo apt-get update"
                      sh "sudo apt-get install yq"
                      sh '''
                           #!/bin/bash
                           echo $GIT_REPO_EMAIL
                           echo $GIT_COMMIT
                           echo $GIT_REPO_BRANCH
                           pwd
                           ls -lth
                           cd /data/builder17/workspace/users-service_develop/yap-ksa-helm/yap-young/app-values/user-service/
                           cat values-dev.yaml
                           yq eval '.image.tag = env(IMAGE_TAG)' -i values-dev.yaml
                           cat values-dev.yaml
                           pwd
                           git add values-dev.yaml
                           git commit -m "Triggered Build"
                           echo $HELM_REPO_URL
                           git push $HELM_REPO_URL
                           '''          
                    }
                  }
               }
    }
}

