REPO_URL = "devdocker.wifa.uni-leipzig.de:5000"
IMAGE_TAG = "de4l-frost-authorization-service"

BUILD = BRANCH_NAME == 'master' ? 'latest' : BRANCH_NAME

node('master') {
    checkout scm
    echo "Build: ${BUILD}"

    withCredentials([usernamePassword(credentialsId: 'docker-registry-devdocker', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
        sh "sudo docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD} ${REPO_URL}"
    }

    stage('Build app') {
        echo "Build: ${BUILD}"
        sh "sudo docker build -t ${REPO_URL}/${IMAGE_TAG}:${BUILD} ."
    }

    stage('Publish to registry') {
        sh "sudo docker push ${REPO_URL}/${IMAGE_TAG}:${BUILD}"
    }
}

