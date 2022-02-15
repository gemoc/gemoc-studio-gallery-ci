
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
					withEnv(["MAVEN_OPTS=-Xmx3000m -XshowSettings:vm -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"]){
						sh "mvn clean verify --show-version "     
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
