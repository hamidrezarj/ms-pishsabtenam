def app
pipeline {
    agent any;
    tools{
        maven 'maven'
        dockerTool 'docker'
    }

    stages {
        stage("pull"){
            steps{
                sh "git clone http://git.sbu.mavaratech.com/sarraf-service/ms-highway.git master"
            }
        }
        stage("Build") {
            steps {
                sh "mvn -DskipTests=true clean package"
            }
        }
        stage('prepare files'){
            steps{
                sh "sed -i 's/{{SRV_NAME}}/${env.JOB_BASE_NAME}/g' deployment.yaml"
                sh "sed -i 's/{{SRV_IMG}}/${env.JOB_BASE_NAME}:${env.BUILD_ID}/g' deployment.yaml"
            }
        }
        stage('docker'){
            steps{
                sh "echo ${env.JOB_BASE_NAME}"
                script{
                    app = docker.build("192.168.1.108:5000/${env.JOB_BASE_NAME}:${env.BUILD_ID}")
                    app.push("latest");
                    app.push("${env.BUILD_ID}")
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    withKubeConfig([credentialsId: 'mykubeconfig2', serverUrl: 'https://192.168.1.124:6443']) {
                                    sh 'kubectl apply -f deployment.yaml'
                                }
                }
            }
        }
    }
}
