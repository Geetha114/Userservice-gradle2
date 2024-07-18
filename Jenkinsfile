def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger']

pipeline {
    agent any 
        

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

            
          
                    
                  
               
    }
}

