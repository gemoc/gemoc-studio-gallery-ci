
pipeline {
	agent any
	options {
		buildDiscarder( logRotator(numToKeepStr:'20', artifactNumToKeepStr: '1'))
		disableConcurrentBuilds()
		timeout(time: 1, unit: 'HOURS')   // timeout on whole pipeline job
	}
    tools {
        maven 'apache-maven-latest'
        jdk 'open-jdk-11'
    }
    stages {
		stage('Verify GEMOC Gallery Status') {
			steps { 
				script {	
					withEnv(["MAVEN_OPTS=-Xmx2000m -XshowSettings:vm"]){
						sh "mvn clean verify --errors --show-version "     
					}
				}
			}
			post {
				always {
					junit '**/target/surefire-reports/TEST-*.xml' 
				}
			}
	 	}
	}
	post {
		fixed {
            slackSend channel: '#ci',
              color: 'good',
              message: "Build fixed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>) is back to normal."
        }
        unstable {
	        slackSend  channel: '#ci',
	          failOnError:true,
              color: 'warning',
	          message: "Build unstable  - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
	    }
        failure {
	        slackSend  channel: '#ci',
	          failOnError:true,
              color: 'danger',
	          message: "Build failed  - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
	    }
    }
}
